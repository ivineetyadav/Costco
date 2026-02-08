package za.co.costcomining.common.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TelemetryDto {
    private String id;
    private String deviceId;
    private String machineId;
    private OffsetDateTime timestamp;
    private Boolean engineRunning;
    private BigDecimal engineHours;
    private BigDecimal fuelLevel;
    private BigDecimal locationLat;
    private BigDecimal locationLng;
    private String payloadJson;
    private OffsetDateTime receivedAt;
}
