package com.pt.recommendation_service.service;

import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.repository.PriceRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class CsvLoaderService implements ApplicationRunner {

    private final PriceRepository repository;
    private final PathMatchingResourcePatternResolver resolver;

    public CsvLoaderService(PriceRepository repository, PathMatchingResourcePatternResolver resolver) {
        this.repository = repository;
        this.resolver = resolver;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Resource[] resources = resolver.getResources("classpath:csv/*.csv");

        for (Resource resource : resources) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                boolean first = true;
                while ((line = br.readLine()) != null) {
                    if (first) { first = false; continue; }
                    String[] parts = line.split(",");
                    Price record = new Price();

                    long millis = Long.parseLong(parts[0]);
                    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
                    record.setDateTime(dateTime);

                    record.setSymbol(parts[1]);
                    record.setPrice(Double.parseDouble(parts[2]));
                    repository.save(record);
                }
            }
        }
    }
}