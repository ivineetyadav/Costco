package za.co.costcomining.common.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UsageReportDto {
    private String id;
    private String contractId;
    private String machineId;
    private String machineName;
    private String vendorId;
    private String vendorName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private BigDecimal totalHours;
    private BigDecimal billableAmount;
    private String currency;
    private LocalDateTime generatedAt;
}
