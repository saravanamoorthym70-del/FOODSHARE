package com.foodshare.controller;

import com.foodshare.dto.MonthlyWasteDiversionReport;
import com.foodshare.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    /**
     * Feature 5: View total food (by weight/quantity) diverted from waste per month.
     * GET /api/reports/monthly?year=2026&month=9
     */
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyWasteDiversionReport> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month) {
        MonthlyWasteDiversionReport report = reportService.getMonthlyReport(year, month);
        return ResponseEntity.ok(report);
    }

    /**
     * Get reports for all months in a given year.
     * GET /api/reports/yearly?year=2026
     */
    @GetMapping("/yearly")
    public ResponseEntity<List<MonthlyWasteDiversionReport>> getYearlyReport(@RequestParam int year) {
        List<MonthlyWasteDiversionReport> reports = reportService.getYearlyReport(year);
        return ResponseEntity.ok(reports);
    }
}
