package za.co.costcomining.common.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DeviceDto {
    private String id;
    private String machineId;
    private String machineName;
    private String deviceSerial;
    private String iotThingName;
    private String status;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime lastSeenAt;
    private LocalDateTime createdAt;
}
