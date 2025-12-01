package com.pt.recommendation_service.repository;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.entity.Price;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PriceRepository extends CrudRepository<Price, Long> {

    @Query("SELECT new com.pt.recommendation_service.dto.CryptoNormalizedRangeDto(p.symbol, (MAX(p.price) - MIN(p.price)) / MIN(p.price)) " +
            "FROM Price p GROUP BY p.symbol ORDER BY ((MAX(p.price) - MIN(p.price)) / MIN(p.price)) DESC")
    List<CryptoNormalizedRangeDto> findNormalizedRangesDesc();
}