package com.pt.recommendation_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CryptoNormalizedRangeDto {
    private String symbol;
    private double normalizedRange;
}