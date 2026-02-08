package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import za.co.costcomining.api.repository.MachineRepository;
import za.co.costcomining.api.repository.UsageReportRepository;
import za.co.costcomining.api.repository.VendorRepository;
import za.co.costcomining.api.security.SecurityUtils;
import za.co.costcomining.common.dto.UsageReportDto;
import za.co.costcomining.common.entity.UsageReport;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BillingService {

    private final UsageReportRepository usageReportRepository;
    private final MachineRepository machineRepository;
    private final VendorRepository vendorRepository;

    public List<UsageReportDto> getReports(LocalDate startDate, LocalDate endDate, String vendorId) {
        List<UsageReport> reports;

        if (SecurityUtils.isVendor()) {
            reports = usageReportRepository.findByVendorIdAndPeriod(
                    SecurityUtils.getCurrentVendorId(), startDate, endDate);
        } else if (vendorId != null) {
            reports = usageReportRepository.findByVendorIdAndPeriod(vendorId, startDate, endDate);
        } else {
            reports = usageReportRepository.findByPeriod(startDate, endDate);
        }

        return reports.stream().map(this::toDto).toList();
    }

    public List<UsageReportDto> getReportsByVendor(String vendorId) {
        return usageReportRepository.findByVendorId(vendorId).stream()
                .map(this::toDto).toList();
    }

    public List<UsageReportDto> getReportsByMachine(String machineId) {
        return usageReportRepository.findByMachineId(machineId).stream()
                .map(this::toDto).toList();
    }

    private UsageReportDto toDto(UsageReport r) {
        String machineName = r.getMachineId() != null ?
                machineRepository.findById(r.getMachineId()).map(m -> m.getName()).orElse(null) : null;
        String vendorName = r.getVendorId() != null ?
                vendorRepository.findById(r.getVendorId()).map(v -> v.getCompanyName()).orElse(null) : null;

        return UsageReportDto.builder()
                .id(r.getId())
                .contractId(r.getContractId())
                .machineId(r.getMachineId())
                .machineName(machineName)
                .vendorId(r.getVendorId())
                .vendorName(vendorName)
                .periodStart(r.getPeriodStart())
                .periodEnd(r.getPeriodEnd())
                .totalHours(r.getTotalHours())
                .billableAmount(r.getBillableAmount())
                .currency(r.getCurrency())
                .generatedAt(r.getGeneratedAt())
                .build();
    }
}
