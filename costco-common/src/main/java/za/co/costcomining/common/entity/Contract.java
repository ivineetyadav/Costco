package za.co.costcomining.common.entity;

import jakarta.persistence.*;
import lombok.*;
import za.co.costcomining.common.util.UlidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contracts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Contract {

    @Id
    @Column(length = 26)
    private String id;

    @Column(name = "machine_id", nullable = false, length = 26)
    private String machineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machine_id", insertable = false, updatable = false)
    private Machine machine;

    @Column(name = "vendor_id", nullable = false, length = 26)
    private String vendorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", insertable = false, updatable = false)
    private Vendor vendor;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "rate_per_hour", nullable = false, precision = 10, scale = 2)
    private BigDecimal ratePerHour;

    @Column(length = 3)
    @Builder.Default
    private String currency = "ZAR";

    @Column(length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UlidGenerator.generate();
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}
