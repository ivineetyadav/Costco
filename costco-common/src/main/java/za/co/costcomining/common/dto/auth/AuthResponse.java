package za.co.costcomining.common.dto.auth;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
    private String role;
    private String email;
    private String fullName;
    private String vendorId;
}
