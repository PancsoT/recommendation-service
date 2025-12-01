package com.pt.recommendation_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CryptoNormalizedRangeDto {
    @Schema(description = "Cryptocurrency symbol, e.g., BTC, ETH", example = "BTC")
    private String symbol;

    @Schema(description = "Normalized range value ((max-min)/min)", example = "0.15")
    private Double normalizedRange;
}
