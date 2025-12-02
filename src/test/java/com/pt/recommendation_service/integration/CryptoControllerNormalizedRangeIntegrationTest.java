package com.pt.recommendation_service.integration;

import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class CryptoControllerNormalizedRangeIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        priceRepository.deleteAll();

        Price btc1 = new Price();
        btc1.setSymbol("BTC");
        btc1.setPrice(100.0);
        btc1.setDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));

        Price btc2 = new Price();
        btc2.setSymbol("BTC");
        btc2.setPrice(200.0);
        btc2.setDateTime(LocalDateTime.of(2022, 1, 2, 0, 0));

        Price eth1 = new Price();
        eth1.setSymbol("ETH");
        eth1.setPrice(50.0);
        eth1.setDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));

        Price eth2 = new Price();
        eth2.setSymbol("ETH");
        eth2.setPrice(100.0);
        eth2.setDateTime(LocalDateTime.of(2022, 1, 2, 0, 0));

        priceRepository.save(btc1);
        priceRepository.save(btc2);
        priceRepository.save(eth1);
        priceRepository.save(eth2);
    }

    @Test
    void getNormalizedRanges_returnsSortedList() throws Exception {
        mockMvc.perform(get("/cryptos/normalized-range")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].symbol", anyOf(is("BTC"), is("ETH"))))
                .andExpect(jsonPath("$[0].normalizedRange", is(1.0)))
                .andExpect(jsonPath("$[1].symbol", anyOf(is("BTC"), is("ETH"))))
                .andExpect(jsonPath("$[1].normalizedRange", is(1.0)));
    }

    @Test
    void getNormalizedRanges_returnsEmptyListIfNoPrices() throws Exception {
        priceRepository.deleteAll();

        mockMvc.perform(get("/cryptos/normalized-range")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}