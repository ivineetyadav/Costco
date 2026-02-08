package za.co.costcomining.common.entity;

import jakarta.persistence.*;
import lombok.*;
import za.co.costcomining.common.util.UlidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "telemetry")
@IdClass(TelemetryId.class)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Telemetry {

    @Id
    @Column(length = 26)
    private String id;

    @Id
    @Column(nullable = false)
    private OffsetDateTime timestamp;

    @Column(name = "device_id", nullable = false, length = 26)
    private String deviceId;

    @Column(name = "machine_id", nullable = false, length = 26)
    private String machineId;

    @Column(name = "engine_running")
    private Boolean engineRunning;

    @Column(name = "engine_hours", precision = 10, scale = 2)
    private BigDecimal engineHours;

    @Column(name = "fuel_level", precision = 5, scale = 2)
    private BigDecimal fuelLevel;

    @Column(name = "location_lat", precision = 9, scale = 6)
    private BigDecimal locationLat;

    @Column(name = "location_lng", precision = 9, scale = 6)
    private BigDecimal locationLng;

    @Column(name = "payload_json", columnDefinition = "jsonb")
    private String payloadJson;

    @Column(name = "received_at")
    private OffsetDateTime receivedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UlidGenerator.generate();
        if (receivedAt == null) receivedAt = OffsetDateTime.now();
    }
}
