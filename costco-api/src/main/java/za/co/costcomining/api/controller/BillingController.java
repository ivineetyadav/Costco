package za.co.costcomining.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.costcomining.api.security.SecurityUtils;
import za.co.costcomining.api.service.BillingService;
import za.co.costcomining.common.dto.UsageReportDto;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingService billingService;

    @GetMapping
    public ResponseEntity<List<UsageReportDto>> getBillingReports(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String vendorId) {

        if (SecurityUtils.isVendor()) {
            vendorId = SecurityUtils.getCurrentVendorId();
        }

        return ResponseEntity.ok(billingService.getReports(startDate, endDate, vendorId));
    }
}
