package za.co.costcomining.common.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class MachineDto {
    private String id;
    private String name;
    private String type;
    private String model;
    private String serialNumber;
    private String powerRating;
    private String status;
    private LocalDateTime createdAt;
}
