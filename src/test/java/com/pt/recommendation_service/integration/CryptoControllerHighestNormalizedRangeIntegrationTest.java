package com.pt.recommendation_service.integration;

import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CryptoControllerHighestNormalizedRangeIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        priceRepository.deleteAll();

        // 2022-01-01
        Price btc1 = new Price();
        btc1.setSymbol("BTC");
        btc1.setPrice(100.0);
        btc1.setDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));

        Price btc2 = new Price();
        btc2.setSymbol("BTC");
        btc2.setPrice(200.0);
        btc2.setDateTime(LocalDateTime.of(2022, 1, 1, 12, 0));

        Price eth1 = new Price();
        eth1.setSymbol("ETH");
        eth1.setPrice(50.0);
        eth1.setDateTime(LocalDateTime.of(2022, 1, 1, 1, 0));

        Price eth2 = new Price();
        eth2.setSymbol("ETH");
        eth2.setPrice(100.0);
        eth2.setDateTime(LocalDateTime.of(2022, 1, 1, 13, 0));

        // 2022-01-02 (másik nap, ne legyen találat)
        Price btc3 = new Price();
        btc3.setSymbol("BTC");
        btc3.setPrice(300.0);
        btc3.setDateTime(LocalDateTime.of(2022, 1, 2, 0, 0));

        priceRepository.save(btc1);
        priceRepository.save(btc2);
        priceRepository.save(eth1);
        priceRepository.save(eth2);
        priceRepository.save(btc3);
    }

    @Test
    void getHighestNormalizedRange_returnsCorrectSymbolAndRange() throws Exception {
        mockMvc.perform(get("/cryptos/normalized-range/highest")
                        .param("date", "2022-01-01")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol", anyOf(is("BTC"), is("ETH"))))
                .andExpect(jsonPath("$.normalizedRange", is(1.0)));
    }

    @Test
    void getHighestNormalizedRange_returnsErrorForInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/cryptos/normalized-range/highest")
                        .param("date", "invalid-date")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid date format")));
    }

    @Test
    void getHighestNormalizedRange_returnsErrorIfNoPricesForDate() throws Exception {
        mockMvc.perform(get("/cryptos/normalized-range/highest")
                        .param("date", "2022-01-03")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("No price data found for date")));
    }
}