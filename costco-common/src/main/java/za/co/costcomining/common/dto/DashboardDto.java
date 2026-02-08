package za.co.costcomining.common.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DashboardDto {
    private long totalMachines;
    private long activeRentals;
    private BigDecimal hoursThisMonth;
    private BigDecimal revenueThisMonth;
    private List<MonthlyRevenue> revenueChart;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class MonthlyRevenue {
        private String month;
        private BigDecimal revenue;
        private BigDecimal hours;
    }
}
