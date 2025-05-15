package com.example.smartlibrary.controller;

import com.example.smartlibrary.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/monthly-activity")
    public ResponseEntity<byte[]> getMonthlyActivityReport(
            @RequestParam int year,
            @RequestParam int month) {

        LocalDate reportDate = LocalDate.of(year, month, 1);
        byte[] pdfBytes = reportService.generateMonthlyActivityReport(reportDate);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=library_report_" + year + "_" + month + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    // Add more report endpoints as needed
}