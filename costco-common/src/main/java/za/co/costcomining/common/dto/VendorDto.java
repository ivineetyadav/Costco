package za.co.costcomining.common.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VendorDto {
    private String id;
    private String companyName;
    private String contactName;
    private String email;
    private String phone;
    private String address;
    private LocalDateTime createdAt;
}
