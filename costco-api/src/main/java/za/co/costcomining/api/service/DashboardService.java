package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.costcomining.api.repository.ContractRepository;
import za.co.costcomining.api.repository.MachineRepository;
import za.co.costcomining.api.repository.UsageReportRepository;
import za.co.costcomining.common.dto.DashboardDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MachineRepository machineRepository;
    private final ContractRepository contractRepository;
    private final UsageReportRepository usageReportRepository;

    public DashboardDto getDashboard() {
        long totalMachines = machineRepository.count();
        long activeRentals = contractRepository.countByStatus("ACTIVE");

        LocalDate monthStart = LocalDate.now().withDayOfMonth(1);
        LocalDate monthEnd = monthStart.plusMonths(1).minusDays(1);

        BigDecimal hoursThisMonth = usageReportRepository.sumHoursByPeriod(monthStart, monthEnd);
        BigDecimal revenueThisMonth = usageReportRepository.sumBillableAmountByPeriod(monthStart, monthEnd);

        // Revenue chart - last 6 months
        List<DashboardDto.MonthlyRevenue> chart = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate start = LocalDate.now().minusMonths(i).withDayOfMonth(1);
            LocalDate end = start.plusMonths(1).minusDays(1);
            BigDecimal revenue = usageReportRepository.sumBillableAmountByPeriod(start, end);
            BigDecimal hours = usageReportRepository.sumHoursByPeriod(start, end);

            chart.add(DashboardDto.MonthlyRevenue.builder()
                    .month(start.getMonth().name().substring(0, 3) + " " + start.getYear())
                    .revenue(revenue)
                    .hours(hours)
                    .build());
        }

        return DashboardDto.builder()
                .totalMachines(totalMachines)
                .activeRentals(activeRentals)
                .hoursThisMonth(hoursThisMonth)
                .revenueThisMonth(revenueThisMonth)
                .revenueChart(chart)
                .build();
    }
}
