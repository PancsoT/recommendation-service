package com.pt.recommendation_service.dto;

import com.pt.recommendation_service.enums.SupportedCryptos;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Data Transfer Object (DTO) representing statistical price information for a cryptocurrency.
 * <p>
 * Contains the symbol and the oldest, newest, minimum, and maximum price values for the given cryptocurrency.
 * </p>
 */
@Data
@AllArgsConstructor
public class CryptoStatsDto {

    /**
     * The cryptocurrency symbol.
     */
    @Schema(description = "Cryptocurrency symbol", example = "BTC")
    private SupportedCryptos symbol;

    /**
     * The oldest price value recorded for the cryptocurrency.
     */
    @Schema(description = "Oldest price value", example = "11000.0")
    private Double oldest;

    /**
     * The newest price value recorded for the cryptocurrency.
     */
    @Schema(description = "Newest price value", example = "5000.0")
    private Double newest;

    /**
     * The lowest price value recorded for the cryptocurrency.
     */
    @Schema(description = "Lowest price value", example = "2000.0")
    private Double min;

    /**
     * The highest price value recorded for the cryptocurrency.
     */
    @Schema(description = "Highest price value", example = "85000.0")
    private Double max;
}