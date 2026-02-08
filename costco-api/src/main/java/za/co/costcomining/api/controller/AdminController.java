package za.co.costcomining.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.costcomining.api.repository.UserRepository;
import za.co.costcomining.api.service.AuthService;
import za.co.costcomining.common.dto.UserDto;
import za.co.costcomining.common.dto.auth.CreateUserRequest;
import za.co.costcomining.common.entity.User;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AuthService authService;
    private final UserRepository userRepository;

    @PostMapping("/users")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserRequest request) {
        User user = authService.createUser(request);
        return ResponseEntity.ok(toDto(user));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> listUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream().map(this::toDto).toList());
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody UserDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getIsActive() != null) user.setIsActive(dto.getIsActive());
        if (dto.getFullName() != null) user.setFullName(dto.getFullName());
        return ResponseEntity.ok(toDto(userRepository.save(user)));
    }

    private UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .email(u.getEmail())
                .fullName(u.getFullName())
                .role(u.getRole())
                .vendorId(u.getVendorId())
                .isActive(u.getIsActive())
                .lastLoginAt(u.getLastLoginAt())
                .createdAt(u.getCreatedAt())
                .build();
    }
}
