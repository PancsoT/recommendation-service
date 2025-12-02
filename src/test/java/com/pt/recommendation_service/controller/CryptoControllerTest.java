package com.pt.recommendation_service.controller;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.dto.CryptoStatsDto;
import com.pt.recommendation_service.enums.SupportedCryptos;
import com.pt.recommendation_service.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CryptoControllerTest {

    private PriceService priceService;
    private CryptoController controller;

    @BeforeEach
    void setUp() {
        priceService = mock(PriceService.class);
        controller = new CryptoController(priceService);
    }

    @Test
    void getNormalizedRanges_returnsListFromService() {
        List<CryptoNormalizedRangeDto> expected = Arrays.asList(
                new CryptoNormalizedRangeDto("BTC", 0.15),
                new CryptoNormalizedRangeDto("ETH", 0.10)
        );
        when(priceService.getNormalizedRangesDesc()).thenReturn(expected);

        List<CryptoNormalizedRangeDto> result = controller.getNormalizedRanges();

        assertEquals(expected, result);
        verify(priceService, times(1)).getNormalizedRangesDesc();
    }

    @Test
    void getStats_returnsStatsFromService() {
        CryptoStatsDto expected = new CryptoStatsDto(SupportedCryptos.BTC, 21000.0, 23000.0, 20000.0, 25000.0);
        when(priceService.getStatsForSymbol("BTC")).thenReturn(expected);

        CryptoStatsDto result = controller.getStats("BTC");

        assertEquals(expected, result);
        verify(priceService, times(1)).getStatsForSymbol("BTC");
    }

    @Test
    void getHighestNormalizedRange_returnsValueFromService() {
        CryptoNormalizedRangeDto expected = new CryptoNormalizedRangeDto("BTC", 0.15);
        when(priceService.getHighestNormalizedRangeForDate("2025-12-01")).thenReturn(expected);

        CryptoNormalizedRangeDto result = controller.getHighestNormalizedRange("2025-12-01");

        assertEquals(expected, result);
        verify(priceService, times(1)).getHighestNormalizedRangeForDate("2025-12-01");
    }
}