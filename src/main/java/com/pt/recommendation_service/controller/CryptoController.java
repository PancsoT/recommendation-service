package com.pt.recommendation_service.controller;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.dto.CryptoStatsDto;
import com.pt.recommendation_service.service.PriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cryptos")
@Tag(name = "Cryptocurrency Endpoints", description = "Endpoints for retrieving cryptocurrency statistics and normalized ranges")
public class CryptoController {

    private final PriceService priceService;

    public CryptoController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/normalized-range")
    @Operation(
            summary = "Get descending sorted list of all cryptocurrencies by normalized range",
            description = "Returns a list of all cryptocurrencies, sorted in descending order by their normalized range ((max-min)/min)."
    )
    public List<CryptoNormalizedRangeDto> getNormalizedRanges() {
        return priceService.getNormalizedRangesDesc();
    }

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
}