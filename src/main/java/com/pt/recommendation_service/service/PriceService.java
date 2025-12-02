package com.pt.recommendation_service.service;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.dto.CryptoStatsDto;
import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.repository.PriceRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public List<CryptoNormalizedRangeDto> getNormalizedRangesDesc() {
        List<Price> allPrices = priceRepository.findAll();

        Map<String, List<Price>> grouped = allPrices.stream()
                .collect(Collectors.groupingBy(Price::getSymbol));

        List<CryptoNormalizedRangeDto> normalizedRangeDtos = getCryptoNormalizedRangeDtos(grouped);

        normalizedRangeDtos.sort(Comparator.comparing(CryptoNormalizedRangeDto::getNormalizedRange).reversed());

        return normalizedRangeDtos;
    }

    public CryptoStatsDto getStatsForSymbol(String symbol) {
        Double oldest = priceRepository.findFirstBySymbolOrderByDateTimeAsc(symbol).getPrice();
        Double newest = priceRepository.findFirstBySymbolOrderByDateTimeDesc(symbol).getPrice();
        Double min = priceRepository.findFirstBySymbolOrderByPriceAsc(symbol).getPrice();
        Double max = priceRepository.findFirstBySymbolOrderByPriceDesc(symbol).getPrice();

        return new CryptoStatsDto(symbol, oldest, newest, min, max);
    }

    public CryptoNormalizedRangeDto getHighestNormalizedRangeForDate(String dateStr) {
        LocalDate date = LocalDate.parse(dateStr);
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        List<Price> pricesInRange = priceRepository.findByDateTimeGreaterThanEqualAndDateTimeLessThan(start, end);

        Map<String, List<Price>> grouped = pricesInRange.stream()
                .collect(Collectors.groupingBy(Price::getSymbol));

        List<CryptoNormalizedRangeDto> normalizedRangeDtos = getCryptoNormalizedRangeDtos(grouped);

        // Find the symbol with the highest normalized range
        return normalizedRangeDtos.stream()
                .max(Comparator.comparing(CryptoNormalizedRangeDto::getNormalizedRange))
                .orElse(null);
    }

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