package com.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndPrice {
    private String stockDate;
    private Double originPrice;
    private Double predictPrice;
}
