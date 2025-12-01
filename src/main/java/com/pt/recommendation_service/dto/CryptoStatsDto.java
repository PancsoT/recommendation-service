package com.pt.recommendation_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CryptoStatsDto {

    @Schema(description = "Cryptocurrency symbol, e.g., BTC, ETH", example = "BTC")
    private String symbol;

    @Schema(description = "Oldest price value", example = "11000.0")
    private Double oldest;

    @Schema(description = "Newest price value", example = "5000.0")
    private Double newest;

    @Schema(description = "Lowest price value", example = "2000.0")
    private Double min;

    @Schema(description = "Highest price value", example = "85000.0")
    private Double max;
}
