package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import za.co.costcomining.api.repository.ContractRepository;
import za.co.costcomining.api.repository.TelemetryRepository;
import za.co.costcomining.api.repository.UsageReportRepository;
import za.co.costcomining.common.entity.Contract;
import za.co.costcomining.common.entity.UsageReport;
import za.co.costcomining.common.util.UlidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UsageCalculationService {

    private final ContractRepository contractRepository;
    private final TelemetryRepository telemetryRepository;
    private final UsageReportRepository usageReportRepository;

    @Scheduled(cron = "0 0 1 * * ?") // Run daily at 1 AM
    @Transactional
    public void calculateDailyUsage() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        calculateUsageForDate(yesterday);
    }

    @Transactional
    public void calculateUsageForDate(LocalDate date) {
        log.info("Calculating usage for date: {}", date);

        List<Contract> activeContracts = contractRepository.findByStatus("ACTIVE");

        for (Contract contract : activeContracts) {
            try {
                calculateForContract(contract, date, date);
            } catch (Exception e) {
                log.error("Failed to calculate usage for contract {}: {}", contract.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public UsageReport calculateForContract(Contract contract, LocalDate periodStart, LocalDate periodEnd) {
        OffsetDateTime start = periodStart.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime end = periodEnd.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        BigDecimal totalHours = telemetryRepository.calculateEngineHours(
                contract.getMachineId(), start, end);

        BigDecimal billableAmount = totalHours.multiply(contract.getRatePerHour());

        UsageReport report = UsageReport.builder()
                .id(UlidGenerator.generate())
                .contractId(contract.getId())
                .machineId(contract.getMachineId())
                .vendorId(contract.getVendorId())
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .totalHours(totalHours)
                .billableAmount(billableAmount)
                .currency(contract.getCurrency())
                .build();

        return usageReportRepository.save(report);
    }

    @Transactional
    public List<UsageReport> generateMonthlyReports(int year, int month) {
        LocalDate periodStart = LocalDate.of(year, month, 1);
        LocalDate periodEnd = periodStart.plusMonths(1).minusDays(1);

        List<Contract> activeContracts = contractRepository.findByStatus("ACTIVE");

        return activeContracts.stream()
                .map(contract -> calculateForContract(contract, periodStart, periodEnd))
                .toList();
    }
}
