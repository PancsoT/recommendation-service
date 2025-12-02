package com.pt.recommendation_service.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import jakarta.servlet.http
        .HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.ServletException;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

/**
 * Servlet filter for IP-based rate limiting using Bucket4j.
 * <p>
 * Limits the number of requests per IP address to 60 requests per minute.
 * If the limit is exceeded, the filter responds with HTTP 429 (Too Many Requests).
 * </p>
 */
@Component
public class RateLimitFilter implements Filter {

    /**
     * Stores a {@link Bucket} (rate limiter) for each IP address.
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Resolves or creates a {@link Bucket} for the given IP address.
     * Each bucket allows up to 60 requests per minute.
     *
     * @param ip the IP address of the client
     * @return the {@link Bucket} associated with the IP
     */
    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, k -> Bucket4j.builder()
                .addLimit(Bandwidth.classic(60, Refill.greedy(60, Duration.ofMinutes(1))))
                .build());
    }

    /**
     * Checks the rate limit for the incoming request's IP address.
     * If the limit is not exceeded, the request is passed along the filter chain.
     * Otherwise, responds with HTTP 429 (Too Many Requests).
     *
     * @param request  the incoming {@link ServletRequest}
     * @param response the outgoing {@link ServletResponse}
     * @param chain    the {@link FilterChain}
     * @throws IOException      if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String ip = ((HttpServletRequest) request).getHeader("X-Forwarded-For");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        Bucket bucket = resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            ((HttpServletResponse) response).setStatus(429);
            response.getWriter().write("Too Many Requests");
        }
    }
}