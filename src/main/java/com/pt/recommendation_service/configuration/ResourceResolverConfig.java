package com.pt.recommendation_service.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Spring configuration class for resource pattern resolving.
 * <p>
 * Registers a {@link PathMatchingResourcePatternResolver} bean
 * </p>
 */
@Configuration
public class ResourceResolverConfig {

    /**
     * Creates a {@link PathMatchingResourcePatternResolver} bean for resource pattern resolution.
     *
     * @return a new instance of {@link PathMatchingResourcePatternResolver}
     */
    @Bean
    public PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver() {
        return new PathMatchingResourcePatternResolver();
    }
}
