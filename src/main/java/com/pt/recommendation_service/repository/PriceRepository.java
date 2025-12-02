package com.pt.recommendation_service.repository;

import com.pt.recommendation_service.entity.Price;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for accessing and querying {@link Price} entities.
 * <p>
 * Provides methods to retrieve price records by symbol, date, and price values.
 * </p>
 */
public interface PriceRepository extends JpaRepository<Price, Long> {

    /**
     * Finds the oldest price record for the given cryptocurrency symbol.
     *
     * @param symbol the cryptocurrency symbol
     * @return the oldest {@link Price} record, or null if none found
     */
    Price findFirstBySymbolOrderByDateTimeAsc(String symbol);

    /**
     * Finds the newest price record for the given cryptocurrency symbol.
     *
     * @param symbol the cryptocurrency symbol
     * @return the newest {@link Price} record, or null if none found
     */
    Price findFirstBySymbolOrderByDateTimeDesc(String symbol);

    /**
     * Finds the price record with the lowest price for the given cryptocurrency symbol.
     *
     * @param symbol the cryptocurrency symbol
     * @return the {@link Price} record with the lowest price, or null if none found
     */
    Price findFirstBySymbolOrderByPriceAsc(String symbol);

    /**
     * Finds the price record with the highest price for the given cryptocurrency symbol.
     *
     * @param symbol the cryptocurrency symbol
     * @return the {@link Price} record with the highest price, or null if none found
     */
    Price findFirstBySymbolOrderByPriceDesc(String symbol);

    /**
     * Finds all price records within the specified date and time range (inclusive start, exclusive end).
     *
     * @param start the start date and time (inclusive)
     * @param end   the end date and time (exclusive)
     * @return a list of {@link Price} records within the specified range
     */
    List<Price> findByDateTimeGreaterThanEqualAndDateTimeLessThan(LocalDateTime start, LocalDateTime end);
}