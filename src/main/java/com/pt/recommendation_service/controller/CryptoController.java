package com.pt.recommendation_service.controller;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.dto.CryptoStatsDto;
import com.pt.recommendation_service.exception.InvalidDateFormatException;
import com.pt.recommendation_service.exception.NoPriceFoundForDateException;
import com.pt.recommendation_service.exception.UnsupportedCryptoException;
import com.pt.recommendation_service.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for cryptocurrency statistics and normalized range endpoints.
 * <p>
 * Provides endpoints to retrieve:
 * <ul>
 *     <li>Descending sorted list of all cryptocurrencies by normalized range</li>
 *     <li>Statistics (oldest, newest, min, max price) for a specific cryptocurrency</li>
 *     <li>The cryptocurrency with the highest normalized range for a specific day</li>
 * </ul>
 * </p>
 */
@RestController
@RequestMapping("/cryptos")
@Tag(name = "Cryptocurrency Endpoints", description = "Endpoints for retrieving cryptocurrency statistics and normalized ranges")
public class CryptoController {

    private final PriceService priceService;

    /**
     * Constructs a new {@code CryptoController} with the given {@link PriceService}.
     *
     * @param priceService the service for cryptocurrency price operations
     */
    public CryptoController(PriceService priceService) {
        this.priceService = priceService;
    }

    /**
     * Returns a descending sorted list of all cryptocurrencies by normalized range ((max-min)/min).
     *
     * @return list of {@link CryptoNormalizedRangeDto} objects
     */
    @GetMapping("/normalized-range")
    @Operation(
            summary = "Get descending sorted list of all cryptocurrencies by normalized range",
            description = "Returns a list of all cryptocurrencies, sorted in descending order by their normalized range ((max-min)/min)."
    )
    public List<CryptoNormalizedRangeDto> getNormalizedRanges() {
        return priceService.getNormalizedRangesDesc();
    }

    /**
     * Returns statistics (oldest, newest, minimum, and maximum price) for the specified cryptocurrency symbol.
     *
     * @param symbol the cryptocurrency symbol (e.g., BTC, ETH)
     * @return {@link CryptoStatsDto} containing the statistics
     */
    @GetMapping("/{symbol}/stats")
    @Operation(
            summary = "Get statistics for a specific cryptocurrency",
            description = "Returns the oldest, newest, minimum, and maximum price for the specified cryptocurrency symbol."
    )
    public CryptoStatsDto getStats(
            @Parameter(
                    description = "Cryptocurrency symbol (e.g., BTC, ETH)",
                    example = "BTC"
            )
            @PathVariable String symbol
    ) {
        return priceService.getStatsForSymbol(symbol);
    }

    /**
     * Returns the cryptocurrency with the highest normalized range ((max-min)/min) for the given date.
     *
     * @param date the date in yyyy-MM-dd format
     * @return {@link CryptoNormalizedRangeDto} for the highest normalized range
     */
    @GetMapping("/normalized-range/highest")
    @Operation(
            summary = "Get the cryptocurrency with the highest normalized range for a specific day",
            description = "Returns the cryptocurrency with the highest normalized range ((max-min)/min) for the given date."
    )
    public CryptoNormalizedRangeDto getHighestNormalizedRange(
            @Parameter(
                    description = "The date for which to find the crypto with the highest normalized range. Format: yyyy-MM-dd",
                    example = "2025-12-01"
            )
            @RequestParam("date") String date
    ) {
        return priceService.getHighestNormalizedRangeForDate(date);
    }

    /**
     * Handles {@link InvalidDateFormatException} thrown when a date string cannot be parsed.
     *
     * @param ex the exception
     * @return a {@link ResponseEntity} with HTTP 400 Bad Request and the error message
     */
    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<String> handleInvalidDateFormatException(InvalidDateFormatException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Handles {@link UnsupportedCryptoException} thrown when an unsupported cryptocurrency symbol is requested.
     *
     * @param ex the exception
     * @return a {@link ResponseEntity} with HTTP 400 Bad Request and the error message
     */
    @ExceptionHandler(UnsupportedCryptoException.class)
    public ResponseEntity<String> handleUnsupportedCryptoException(UnsupportedCryptoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /**
     * Handles {@link NoPriceFoundForDateException} thrown when no price data is found for a given date.
     *
     * @param ex the exception
     * @return a {@link ResponseEntity} with HTTP 404 Not Found and the error message
     */
    @ExceptionHandler(NoPriceFoundForDateException.class)
    public ResponseEntity<String> handleNoPriceFoundForDateException(NoPriceFoundForDateException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}