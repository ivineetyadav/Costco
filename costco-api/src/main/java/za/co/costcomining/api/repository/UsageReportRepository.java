package za.co.costcomining.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import za.co.costcomining.common.entity.UsageReport;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface UsageReportRepository extends JpaRepository<UsageReport, String> {
    List<UsageReport> findByVendorId(String vendorId);
    List<UsageReport> findByMachineId(String machineId);
    List<UsageReport> findByContractId(String contractId);

    @Query("SELECT u FROM UsageReport u WHERE u.periodStart >= :start AND u.periodEnd <= :end")
    List<UsageReport> findByPeriod(LocalDate start, LocalDate end);

    @Query("SELECT u FROM UsageReport u WHERE u.vendorId = :vendorId AND u.periodStart >= :start AND u.periodEnd <= :end")
    List<UsageReport> findByVendorIdAndPeriod(String vendorId, LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(u.billableAmount), 0) FROM UsageReport u WHERE u.periodStart >= :start AND u.periodEnd <= :end")
    BigDecimal sumBillableAmountByPeriod(LocalDate start, LocalDate end);

    @Query("SELECT COALESCE(SUM(u.totalHours), 0) FROM UsageReport u WHERE u.periodStart >= :start AND u.periodEnd <= :end")
    BigDecimal sumHoursByPeriod(LocalDate start, LocalDate end);
}
