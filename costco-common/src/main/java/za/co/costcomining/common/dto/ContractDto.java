package za.co.costcomining.common.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ContractDto {
    private String id;
    private String machineId;
    private String machineName;
    private String vendorId;
    private String vendorName;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal ratePerHour;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
}
