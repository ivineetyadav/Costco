package za.co.costcomining.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import za.co.costcomining.common.dto.UsageReportDto;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportExportService {

    public byte[] generateExcelReport(List<UsageReportDto> reports, String title) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Usage Report");

            // Header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            // Header row
            Row headerRow = sheet.createRow(0);
            String[] headers = {"Machine", "Vendor", "Period Start", "Period End",
                    "Total Hours", "Rate", "Billable Amount", "Currency"};

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 1;
            for (UsageReportDto report : reports) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(report.getMachineName() != null ? report.getMachineName() : "");
                row.createCell(1).setCellValue(report.getVendorName() != null ? report.getVendorName() : "");
                row.createCell(2).setCellValue(report.getPeriodStart() != null ? report.getPeriodStart().toString() : "");
                row.createCell(3).setCellValue(report.getPeriodEnd() != null ? report.getPeriodEnd().toString() : "");
                row.createCell(4).setCellValue(report.getTotalHours() != null ? report.getTotalHours().doubleValue() : 0);
                row.createCell(5).setCellValue("");
                row.createCell(6).setCellValue(report.getBillableAmount() != null ? report.getBillableAmount().doubleValue() : 0);
                row.createCell(7).setCellValue(report.getCurrency() != null ? report.getCurrency() : "ZAR");
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }
}
