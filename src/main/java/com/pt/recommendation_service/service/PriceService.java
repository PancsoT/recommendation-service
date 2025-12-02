package com.pt.recommendation_service.service;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.dto.CryptoStatsDto;
import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.enums.SupportedCryptos;
import com.pt.recommendation_service.exception.InvalidDateFormatException;
import com.pt.recommendation_service.exception.NoPriceFoundForDateException;
import com.pt.recommendation_service.repository.PriceRepository;
import com.pt.recommendation_service.validator.CryptoValidator;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for cryptocurrency price operations and statistics.
 * <p>
 * Provides methods to calculate normalized ranges, retrieve statistics for a symbol,
 * and find the cryptocurrency with the highest normalized range for a specific date.
 * </p>
 */
@Service
public class PriceService {

    private final PriceRepository priceRepository;
    private final CryptoValidator cryptoValidator;

    /**
     * Constructs a new {@code PriceService} with the required dependencies.
     *
     * @param priceRepository the repository for accessing price data
     * @param cryptoValidator the validator for supported cryptocurrency symbols
     */
    public PriceService(PriceRepository priceRepository, CryptoValidator cryptoValidator) {
        this.priceRepository = priceRepository;
        this.cryptoValidator = cryptoValidator;
    }

    /**
     * Returns a descending sorted list of all cryptocurrencies by normalized range ((max-min)/min).
     *
     * @return list of {@link CryptoNormalizedRangeDto} objects
     */
    public List<CryptoNormalizedRangeDto> getNormalizedRangesDesc() {
        List<Price> allPrices = priceRepository.findAll();

        Map<String, List<Price>> grouped = allPrices.stream()
                .collect(Collectors.groupingBy(Price::getSymbol));

        List<CryptoNormalizedRangeDto> normalizedRangeDtos = getCryptoNormalizedRangeDtos(grouped);

        normalizedRangeDtos.sort(Comparator.comparing(CryptoNormalizedRangeDto::getNormalizedRange).reversed());

        return normalizedRangeDtos;
    }

    /**
     * Returns statistics (oldest, newest, minimum, and maximum price) for the specified cryptocurrency symbol.
     * Throws an exception if the symbol is not supported.
     *
     * @param symbol the cryptocurrency symbol
     * @return {@link CryptoStatsDto} containing the statistics
     */
    public CryptoStatsDto getStatsForSymbol(String symbol) {
        SupportedCryptos crypto = cryptoValidator.validateSymbol(symbol);
        Double oldest = priceRepository.findFirstBySymbolOrderByDateTimeAsc(symbol).getPrice();
        Double newest = priceRepository.findFirstBySymbolOrderByDateTimeDesc(symbol).getPrice();
        Double min = priceRepository.findFirstBySymbolOrderByPriceAsc(symbol).getPrice();
        Double max = priceRepository.findFirstBySymbolOrderByPriceDesc(symbol).getPrice();

        return new CryptoStatsDto(crypto, oldest, newest, min, max);
    }

    /**
     * Returns the cryptocurrency with the highest normalized range ((max-min)/min) for the given date.
     * Throws an exception if the date format is invalid or if no price data is found for the date.
     *
     * @param dateStr the date in yyyy-MM-dd format
     * @return {@link CryptoNormalizedRangeDto} for the highest normalized range
     * @throws InvalidDateFormatException   if
    the date format is invalid
     * @throws NoPriceFoundForDateException if no price data is found for the date
     */
    public CryptoNormalizedRangeDto getHighestNormalizedRangeForDate(String dateStr) {
        LocalDate date;
        try {
            date = LocalDate.parse(dateStr);
        } catch (Exception e) {
            throw new InvalidDateFormatException("Invalid date format: " + dateStr + ". Expected format: yyyy-MM-dd");
        }
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Price> pricesInRange = priceRepository.findByDateTimeGreaterThanEqualAndDateTimeLessThan(start, end);

        if (pricesInRange.isEmpty()) {
            throw new NoPriceFoundForDateException(dateStr);
        }

        Map<String, List<Price>> grouped = pricesInRange.stream()
                .collect(Collectors.groupingBy(Price::getSymbol));

        List<CryptoNormalizedRangeDto> normalizedRangeDtos = getCryptoNormalizedRangeDtos(grouped);

        // Find the symbol with the highest normalized range
        return normalizedRangeDtos.stream()
                .max(Comparator.comparing(CryptoNormalizedRangeDto::getNormalizedRange))
                .orElse(null);
    }

    /**
     * Helper method to calculate normalized range DTOs for each symbol in the grouped price data.
     *
     * @param grouped a map of symbol to list of prices
     * @return list of {@link CryptoNormalizedRangeDto} objects
     */
    private List<CryptoNormalizedRangeDto> getCryptoNormalizedRangeDtos(Map<String, List<Price>> grouped) {
        List<CryptoNormalizedRangeDto> result = new ArrayList<>();
        for (Map.Entry<String, List<Price>> entry : grouped.entrySet()) {
            String symbol = entry.getKey();
            List<Price> prices = entry.getValue();

            OptionalDouble minValue = prices.stream().mapToDouble(Price::getPrice).min();
            OptionalDouble maxValue = prices.stream().mapToDouble(Price::getPrice).max();

            if (minValue.isPresent() && maxValue.isPresent() && minValue.getAsDouble() != 0.0) {
                double min = minValue.getAsDouble();
                double max = maxValue.getAsDouble();
                double normalizedRange = (max - min) / min;
                result.add(new CryptoNormalizedRangeDto(symbol, normalizedRange));
            }
        }
        return result;
    }
}