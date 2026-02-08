package za.co.costcomining.common.entity;

import jakarta.persistence.*;
import lombok.*;
import za.co.costcomining.common.util.UlidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_reports")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UsageReport {

    @Id
    @Column(length = 26)
    private String id;

    @Column(name = "contract_id", length = 26)
    private String contractId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", insertable = false, updatable = false)
    private Contract contract;

    @Column(name = "machine_id", length = 26)
    private String machineId;

    @Column(name = "vendor_id", length = 26)
    private String vendorId;

    @Column(name = "period_start")
    private LocalDate periodStart;

    @Column(name = "period_end")
    private LocalDate periodEnd;

    @Column(name = "total_hours", precision = 10, scale = 2)
    private BigDecimal totalHours;

    @Column(name = "billable_amount", precision = 12, scale = 2)
    private BigDecimal billableAmount;

    @Column(length = 3)
    @Builder.Default
    private String currency = "ZAR";

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UlidGenerator.generate();
        if (generatedAt == null) generatedAt = LocalDateTime.now();
    }
}
