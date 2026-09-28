package com.foodshare.service;

import com.foodshare.dto.MonthlyWasteDiversionReport;
import com.foodshare.entity.FoodListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private FoodListingService foodListingService;

    /**
     * View total food (by weight/quantity) diverted from waste per month.
     * Returns a report for the specified year and month.
     */
    public MonthlyWasteDiversionReport getMonthlyReport(int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);

        List<FoodListing> collectedListings = foodListingService.getCollectedListingsBetween(start, end);

        double totalQuantity = collectedListings.stream()
                .mapToDouble(FoodListing::getQuantity)
                .sum();

        // Determine the most common unit
        String commonUnit = collectedListings.stream()
                .collect(Collectors.groupingBy(FoodListing::getUnit, Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("kg");

        MonthlyWasteDiversionReport report = new MonthlyWasteDiversionReport();
        report.setYear(year);
        report.setMonth(month);
        report.setTotalListingsCollected(collectedListings.size());
        report.setTotalQuantityDiverted(totalQuantity);
        report.setUnit(commonUnit);

        return report;
    }

    /**
     * Get reports for all months in a given year.
     */
    public List<MonthlyWasteDiversionReport> getYearlyReport(int year) {
        return java.util.stream.IntStream.rangeClosed(1, 12)
                .mapToObj(month -> getMonthlyReport(year, month))
                .collect(Collectors.toList());
    }
}
