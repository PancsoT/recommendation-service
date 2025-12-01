package com.pt.recommendation_service.repository;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.entity.Price;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository extends CrudRepository<Price, Long> {

    @Query("SELECT new com.pt.recommendation_service.dto.CryptoNormalizedRangeDto(p.symbol, (MAX(p.price) - MIN(p.price)) / MIN(p.price)) " +
            "FROM Price p GROUP BY p.symbol ORDER BY ((MAX(p.price) - MIN(p.price)) / MIN(p.price)) DESC")
    List<CryptoNormalizedRangeDto> findNormalizedRangesDesc();

    Price findFirstBySymbolOrderByDateTimeAsc(String symbol);
    Price findFirstBySymbolOrderByDateTimeDesc(String symbol);
    Price findFirstBySymbolOrderByPriceAsc(String symbol);
    Price findFirstBySymbolOrderByPriceDesc(String symbol);

    @Query("SELECT new com.pt.recommendation_service.dto.CryptoNormalizedRangeDto(p.symbol, (MAX(p.price) - MIN(p.price)) / MIN(p.price)) " +
            "FROM Price p WHERE p.dateTime >= :start AND p.dateTime < :end GROUP BY p.symbol ORDER BY ((MAX(p.price) - MIN(p.price)) / MIN(p.price)) DESC")
    List<CryptoNormalizedRangeDto> findNormalizedRangesByDateDesc(LocalDateTime start, LocalDateTime end);
}