package com.pt.recommendation_service.service;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.dto.CryptoStatsDto;
import com.pt.recommendation_service.repository.PriceRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PriceService {

    private final PriceRepository priceRepository;

    public PriceService(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    public List<CryptoNormalizedRangeDto> getNormalizedRangesDesc() {
        return priceRepository.findNormalizedRangesDesc();
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
        List<CryptoNormalizedRangeDto> list = priceRepository.findNormalizedRangesByDateDesc(start, end);
        return list.isEmpty() ? null : list.getFirst();
    }
}