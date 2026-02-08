package za.co.costcomining.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import za.co.costcomining.api.service.BillingService;
import za.co.costcomining.api.service.ReportExportService;
import za.co.costcomining.api.service.UsageCalculationService;
import za.co.costcomining.common.dto.UsageReportDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class UsageReportController {

    private final BillingService billingService;
    private final UsageCalculationService usageCalculationService;
    private final ReportExportService reportExportService;

    @GetMapping
    public ResponseEntity<List<UsageReportDto>> getReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String vendorId) {
        return ResponseEntity.ok(billingService.getReports(startDate, endDate, vendorId));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<UsageReportDto>> getByVendor(@PathVariable String vendorId) {
        return ResponseEntity.ok(billingService.getReportsByVendor(vendorId));
    }

    @GetMapping("/machine/{machineId}")
    public ResponseEntity<List<UsageReportDto>> getByMachine(@PathVariable String machineId) {
        return ResponseEntity.ok(billingService.getReportsByMachine(machineId));
    }

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UsageReportDto>> generateMonthly(
            @RequestParam int year, @RequestParam int month) {
        var reports = usageCalculationService.generateMonthlyReports(year, month);
        return ResponseEntity.ok(reports.stream().map(r -> UsageReportDto.builder()
                .id(r.getId())
                .contractId(r.getContractId())
                .machineId(r.getMachineId())
                .vendorId(r.getVendorId())
                .periodStart(r.getPeriodStart())
                .periodEnd(r.getPeriodEnd())
                .totalHours(r.getTotalHours())
                .billableAmount(r.getBillableAmount())
                .currency(r.getCurrency())
                .generatedAt(r.getGeneratedAt())
                .build()).toList());
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String vendorId) throws Exception {
        List<UsageReportDto> reports = billingService.getReports(startDate, endDate, vendorId);
        byte[] excel = reportExportService.generateExcelReport(reports, "Usage Report");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=usage-report.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excel);
    }
}
