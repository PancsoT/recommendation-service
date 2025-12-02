package com.pt.recommendation_service.service;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.dto.CryptoStatsDto;
import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class PriceServiceTest {

    private PriceRepository priceRepository;
    private PriceService priceService;

    @BeforeEach
    void setUp() {
        priceRepository = mock(PriceRepository.class);
        priceService = new PriceService(priceRepository);
    }

    @Test
    void getNormalizedRangesDesc_returnsCorrectRangesAndOrder() {
        // Given
        Price btc1 = new Price();
        btc1.setSymbol("BTC");
        btc1.setPrice(100.0);

        Price btc2 = new Price();
        btc2.setSymbol("BTC");
        btc2.setPrice(200.0);

        Price eth1 = new Price();
        eth1.setSymbol("ETH");
        eth1.setPrice(50.0);

        Price eth2 = new Price();
        eth2.setSymbol("ETH");
        eth2.setPrice(100.0);

        List<Price> prices = Arrays.asList(btc1, btc2, eth1, eth2);
        when(priceRepository.findAll()).thenReturn(prices);

        // When
        List<CryptoNormalizedRangeDto> result = priceService.getNormalizedRangesDesc();

        // Then
        assertEquals(2, result.size());

        // BTC: (200-100)/100 = 1.0
        // ETH: (100-50)/50 = 1.0
        assertEquals("BTC", result.get(0).getSymbol());
        assertEquals(1.0, result.get(0).getNormalizedRange());
        assertEquals("ETH", result.get(1).getSymbol());
        assertEquals(1.0, result.get(1).getNormalizedRange());
    }

    @Test
    void getNormalizedRangesDesc_skipsSymbolWithZeroMinPrice() {
        // Given
        Price btc1 = new Price();
        btc1.setSymbol("BTC");
        btc1.setPrice(0.0);

        Price btc2 = new Price();
        btc2.setSymbol("BTC");
        btc2.setPrice(100.0);

        List<Price> prices = Arrays.asList(btc1, btc2);
        when(priceRepository.findAll()).thenReturn(prices);

        // When
        List<CryptoNormalizedRangeDto> result = priceService.getNormalizedRangesDesc();

        // Then
        // Should skip BTC because min price is zero (division by zero)
        assertTrue(result.isEmpty());
    }

    @Test
    void getNormalizedRangesDesc_returnsEmptyListIfNoPrices() {
        // Given
        when(priceRepository.findAll()).thenReturn(Arrays.asList());

        // When
        List<CryptoNormalizedRangeDto> result = priceService.getNormalizedRangesDesc();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getHighestNormalizedRangeForDate_returnsHighestRange() {
        // Given
        LocalDateTime start = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 1, 2, 0, 0);

        Price btc1 = new Price();
        btc1.setSymbol("BTC");
        btc1.setPrice(100.0);
        btc1.setDateTime(start.plusHours(1));

        Price btc2 = new Price();
        btc2.setSymbol("BTC");
        btc2.setPrice(200.0);
        btc2.setDateTime(start.plusHours(2));

        Price eth1 = new Price();
        eth1.setSymbol("ETH");
        eth1.setPrice(50.0);
        eth1.setDateTime(start.plusHours(3));

        Price eth2 = new Price();
        eth2.setSymbol("ETH");
        eth2.setPrice(100.0);
        eth2.setDateTime(start.plusHours(4));

        List<Price> prices = Arrays.asList(btc1, btc2, eth1, eth2);
        when(priceRepository.findByDateTimeGreaterThanEqualAndDateTimeLessThan(start, end)).thenReturn(prices);

        // When
        CryptoNormalizedRangeDto result = priceService.getHighestNormalizedRangeForDate("2022-01-01");

        // Then
        assertNotNull(result);
        // Both BTC and ETH have normalized range 1.0, but BTC comes first in the list
        assertTrue(result.getNormalizedRange() == 1.0);
        assertTrue(result.getSymbol().equals("BTC") || result.getSymbol().equals("ETH"));
    }

    @Test
    void getHighestNormalizedRangeForDate_returnsNullIfNoPrices() {
        LocalDateTime start = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 1, 2, 0, 0);

        when(priceRepository.findByDateTimeGreaterThanEqualAndDateTimeLessThan(start, end)).thenReturn(Arrays.asList());

        CryptoNormalizedRangeDto result = priceService.getHighestNormalizedRangeForDate("2022-01-01");

        assertNull(result);
    }

    @Test
    void getHighestNormalizedRangeForDate_skipsSymbolWithZeroMinPrice() {
        LocalDateTime start = LocalDateTime.of(2022, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2022, 1, 2, 0, 0);

        Price btc1 = new Price();
        btc1.setSymbol("BTC");
        btc1.setPrice(0.0);
        btc1.setDateTime(start.plusHours(1));

        Price btc2 = new Price();
        btc2.setSymbol("BTC");
        btc2.setPrice(100.0);
        btc2.setDateTime(start.plusHours(2));

        List<Price> prices = Arrays.asList(btc1, btc2);
        when(priceRepository.findByDateTimeGreaterThanEqualAndDateTimeLessThan(start, end)).thenReturn(prices);

        CryptoNormalizedRangeDto result = priceService.getHighestNormalizedRangeForDate("2022-01-01");

        // Should skip BTC because min price is zero (division by zero)
        assertNull(result);
    }

    @Test
    void getStatsForSymbol_returnsCorrectStats() {
        String symbol = "BTC";

        Price oldest = new Price();
        oldest.setPrice(100.0);

        Price newest = new Price();
        newest.setPrice(200.0);

        Price min = new Price();
        min.setPrice(90.0);

        Price max = new Price();
        max.setPrice(210.0);

        when(priceRepository.findFirstBySymbolOrderByDateTimeAsc(symbol)).thenReturn(oldest);
        when(priceRepository.findFirstBySymbolOrderByDateTimeDesc(symbol)).thenReturn(newest);
        when(priceRepository.findFirstBySymbolOrderByPriceAsc(symbol)).thenReturn(min);
        when(priceRepository.findFirstBySymbolOrderByPriceDesc(symbol)).thenReturn(max);

        CryptoStatsDto result = priceService.getStatsForSymbol(symbol);

        assertNotNull(result);
        assertEquals(symbol, result.getSymbol());
        assertEquals(100.0, result.getOldest());
        assertEquals(200.0, result.getNewest());
        assertEquals(90.0, result.getMin());
        assertEquals(210.0, result.getMax());
    }

    @Test
    void getStatsForSymbol_handlesNullPrices() {
        String symbol = "BTC";

        when(priceRepository.findFirstBySymbolOrderByDateTimeAsc(symbol)).thenReturn(null);
        when(priceRepository.findFirstBySymbolOrderByDateTimeDesc(symbol)).thenReturn(null);
        when(priceRepository.findFirstBySymbolOrderByPriceAsc(symbol)).thenReturn(null);
        when(priceRepository.findFirstBySymbolOrderByPriceDesc(symbol)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> priceService.getStatsForSymbol(symbol));
    }
}