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
class CryptoControllerStatsIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        priceRepository.deleteAll();

        Price oldest = new Price();
        oldest.setSymbol("BTC");
        oldest.setPrice(100.0);
        oldest.setDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));

        Price newest = new Price();
        newest.setSymbol("BTC");
        newest.setPrice(200.0);
        newest.setDateTime(LocalDateTime.of(2022, 1, 2, 0, 0));

        Price min = new Price();
        min.setSymbol("BTC");
        min.setPrice(90.0);
        min.setDateTime(LocalDateTime.of(2022, 1, 1, 12, 0));

        Price max = new Price();
        max.setSymbol("BTC");
        max.setPrice(210.0);
        max.setDateTime(LocalDateTime.of(2022, 1, 2, 12, 0));

        priceRepository.save(oldest);
        priceRepository.save(newest);
        priceRepository.save(min);
        priceRepository.save(max);
    }

    @Test
    void getStats_returnsCorrectStats() throws Exception {
        mockMvc.perform(get("/cryptos/BTC/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol", is("BTC")))
                .andExpect(jsonPath("$.oldest", is(100.0)))
                .andExpect(jsonPath("$.newest", is(210.0)))
                .andExpect(jsonPath("$.min", is(90.0)))
                .andExpect(jsonPath("$.max", is(210.0)));
    }

    @Test
    void getStats_returnsErrorForUnsupportedSymbol() throws Exception {
        mockMvc.perform(get("/cryptos/INVALID/stats")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Crypto is not supported")));
    }
}