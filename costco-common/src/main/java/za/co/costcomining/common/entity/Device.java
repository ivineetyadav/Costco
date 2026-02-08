package za.co.costcomining.common.entity;

import jakarta.persistence.*;
import lombok.*;
import za.co.costcomining.common.util.UlidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "devices")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Device {

    @Id
    @Column(length = 26)
    private String id;

    @Column(name = "machine_id", length = 26)
    private String machineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", insertable = false, updatable = false)
    private Machine machine;

    @Column(name = "device_serial", unique = true, nullable = false, length = 100)
    private String deviceSerial;

    @Column(name = "iot_thing_name", unique = true, nullable = false, length = 100)
    private String iotThingName;

    @Column(length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "approved_by", length = 26)
    private String approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UlidGenerator.generate();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
