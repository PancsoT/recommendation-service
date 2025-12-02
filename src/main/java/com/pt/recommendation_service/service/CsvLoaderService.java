package com.pt.recommendation_service.service;

import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.repository.PriceRepository;
import com.pt.recommendation_service.validator.CryptoValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Service that loads cryptocurrency price data from CSV files at application startup.
 * <p>
 * Scans the {@code resources/csv} directory for CSV files, parses each line, validates the symbol,
 * and saves valid price records to the database. Logs warnings and errors for unsupported symbols
 * and parsing failures, but continues processing remaining files and lines.
 * </p>
 */
@Component
public class CsvLoaderService implements ApplicationRunner {

    private final PriceRepository repository;
    private final PathMatchingResourcePatternResolver resolver;
    private final CryptoValidator cryptoValidator;

    public CsvLoaderService(PriceRepository repository, PathMatchingResourcePatternResolver resolver, CryptoValidator cryptoValidator) {
        this.repository = repository;
        this.resolver = resolver;
        this.cryptoValidator = cryptoValidator;
    }

    /**
     * Loads and processes all CSV files from the {@code resources/csv} directory.
     * <ul>
     *     <li>Logs a warning if no CSV files are found.</li>
     *     <li>For each file, parses each line (skipping the header), validates the symbol,
     *         and saves valid price records to the database.</li>
     *     <li>Logs warnings for unsupported symbols and errors for parsing failures, but continues processing.</li>
     * </ul>
     *
     * @param args application arguments (not used)
     * @throws Exception if an unrecoverable error occurs during file processing
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        Logger logger = LoggerFactory.getLogger(CsvLoaderService.class);
        Resource[] resources = resolver.getResources("classpath:csv/*.csv");

        if (resources.length == 0) {
            logger.warn("No CSV files found in resources/csv directory.");
        }

        for (Resource resource : resources) {
            String fileName = resource.getFilename();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
                String line;
                boolean first = true;
                int lineNumber = 0;
                while ((line = br.readLine()) != null) {
                    lineNumber++;
                    if (first) { first = false; continue; }
                    try {
                        String[] parts = line.split(",");
                        if (cryptoValidator.isSymbolValid(parts[1])) {
                            Price record = new Price();

                            long millis = Long.parseLong(parts[0]);
                            LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC);
                            record.setDateTime(dateTime);

                            record.setSymbol(parts[1]);
                            record.setPrice(Double.parseDouble(parts[2]));
                            repository.save(record);
                        } else {
                            logger.warn("Crypto symbol {} is not supported in line {} in file {}", parts[1], lineNumber, fileName);
                        }
                    } catch (Exception e) {
                        logger.error("Failed to parse line {} in file '{}': '{}'. Error: {}", lineNumber, fileName, line, e.getMessage());
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to process file '{}'. Error: {}", fileName, e.getMessage());
            }
        }
    }
}