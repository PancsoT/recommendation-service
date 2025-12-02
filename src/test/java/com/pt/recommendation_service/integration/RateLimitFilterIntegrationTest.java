package com.pt.recommendation_service.integration;

import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.filter.RateLimitFilter;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
class RateLimitFilterIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @Autowired
    private RateLimitFilter rateLimitFilter;

    @Autowired
    private PriceRepository priceRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter(rateLimitFilter)
                .build();

        priceRepository.deleteAll();

        Price btc1 = new Price();
        btc1.setSymbol("BTC");
        btc1.setPrice(100.0);
        btc1.setDateTime(LocalDateTime.of(2022, 1, 1, 0, 0));
        priceRepository.save(btc1);
    }

    @Test
    void rateLimit_blocksAfterThreshold() throws Exception {
        String ip = "192.168.1.100";
        int limit = 60;

        for (int i = 0; i < limit; i++) {
            mockMvc.perform(get("/cryptos/normalized-range")
                            .accept(MediaType.APPLICATION_JSON)
                            .header("X-Forwarded-For", ip))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/cryptos/normalized-range")
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Forwarded-For", ip))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("Too Many Requests"));
    }
}