package com.pt.recommendation_service.repository;

import com.pt.recommendation_service.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceRepository extends JpaRepository<Price, Long> {

    Price findFirstBySymbolOrderByDateTimeAsc(String symbol);
    Price findFirstBySymbolOrderByDateTimeDesc(String symbol);
    Price findFirstBySymbolOrderByPriceAsc(String symbol);
    Price findFirstBySymbolOrderByPriceDesc(String symbol);

    List<Price> findByDateTimeGreaterThanEqualAndDateTimeLessThan(LocalDateTime start, LocalDateTime end);
}