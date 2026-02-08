package za.co.costcomining.common.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String email;
    private String fullName;
    private String role;
    private String vendorId;
    private String vendorName;
    private Boolean isActive;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
}
