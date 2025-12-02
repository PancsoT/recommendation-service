package com.pt.recommendation_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing the normalized range of a cryptocurrency.
 * <p>
 * The normalized range is calculated as (max-min)/min for a given cryptocurrency symbol.
 * </p>
 */
@Data
@AllArgsConstructor
public class CryptoNormalizedRangeDto {

    /**
     * The cryptocurrency symbol (e.g., BTC, ETH).
     */
    @Schema(description = "Cryptocurrency symbol, e.g., BTC, ETH", example = "BTC")
    private String symbol;

    /**
     * The normalized range value, calculated as (max-min)/min.
     */
    @Schema(description = "Normalized range value ((max-min)/min)", example = "0.15")
    private Double normalizedRange;
}