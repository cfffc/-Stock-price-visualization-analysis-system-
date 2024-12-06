package com.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AverageData {
    private String stockDate;
    private Double m5;
    private Double m10;
    private Double m20;
    private Double m30;
    private Double m60;
    private Double m120;
    private Double m250;
}
