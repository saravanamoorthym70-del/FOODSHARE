package com.foodshare.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyWasteDiversionReport {
    private int year;
    private int month;
    private long totalListingsCollected;
    private double totalQuantityDiverted;
    private String unit;
}
