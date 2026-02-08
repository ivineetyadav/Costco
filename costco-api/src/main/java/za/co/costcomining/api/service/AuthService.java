package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.costcomining.api.repository.UserRepository;
import za.co.costcomining.api.security.JwtTokenProvider;
import za.co.costcomining.common.dto.auth.AuthResponse;
import za.co.costcomining.common.dto.auth.CreateUserRequest;
import za.co.costcomining.common.dto.auth.LoginRequest;
import za.co.costcomining.common.dto.auth.RefreshRequest;
import za.co.costcomining.common.entity.User;
import za.co.costcomining.common.util.UlidGenerator;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.getIsActive()) {
            throw new IllegalArgumentException("Account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String accessToken = tokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole(), user.getVendorId());
        String refreshToken = tokenProvider.generateRefreshToken(user.getId());

        user.setRefreshToken(refreshToken);
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(tokenProvider.getAccessTokenExpirationMs() / 1000)
                .role(user.getRole())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .vendorId(user.getVendorId())
                .build();
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String userId = tokenProvider.getUserIdFromToken(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!request.getRefreshToken().equals(user.getRefreshToken())) {
            throw new IllegalArgumentException("Refresh token has been revoked");
        }

        String accessToken = tokenProvider.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole(), user.getVendorId());
        String newRefreshToken = tokenProvider.generateRefreshToken(user.getId());

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(tokenProvider.getAccessTokenExpirationMs() / 1000)
                .role(user.getRole())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .vendorId(user.getVendorId())
                .build();
    }

    @Transactional
    public void logout(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setRefreshToken(null);
        userRepository.save(user);
    }

    @Transactional
    public User createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = User.builder()
                .id(UlidGenerator.generate())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .vendorId(request.getVendorId())
                .isActive(true)
                .build();

        return userRepository.save(user);
    }
}
