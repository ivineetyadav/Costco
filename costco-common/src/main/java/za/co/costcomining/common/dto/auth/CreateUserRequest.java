package za.co.costcomining.common.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CreateUserRequest {
    @NotBlank @Email
    private String email;
    @NotBlank
    private String fullName;
    @NotBlank
    private String password;
    @NotBlank
    private String role;
    private String vendorId;
}
