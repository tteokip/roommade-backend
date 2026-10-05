package com.roommade.global.monitoring;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.prometheus.metrics.core.metrics.Counter;
import io.prometheus.metrics.core.metrics.Histogram;

public class HttpMetricsFilter implements Filter {

    private static final Pattern NUMERIC_IDENTIFIER = Pattern.compile("/(?=\\d{2,}(?:/|$))\\d+");
    private static final Pattern UUID_IDENTIFIER = Pattern.compile("/[0-9a-fA-F]{8}-[0-9a-fA-F-]{27,}(?=/|$)");

    private static final Counter HTTP_REQUESTS = Counter.builder()
            .name("roommade_http_requests_total")
            .help("Total HTTP requests handled by RoomMade.")
            .labelNames("method", "path", "status")
            .register();

    private static final Histogram HTTP_REQUEST_DURATION = Histogram.builder()
            .name("roommade_http_request_duration_seconds")
            .help("HTTP request duration in seconds for RoomMade.")
            .labelNames("method", "path", "status")
            .classicOnly()
            .register();

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        if ("/metrics".equals(httpRequest.getRequestURI().substring(httpRequest.getContextPath().length()))) {
            chain.doFilter(request, response);
            return;
        }

        long startedAt = System.nanoTime();
        boolean requestFailed = false;
        try {
            chain.doFilter(request, response);
        } catch (IOException | ServletException exception) {
            requestFailed = true;
            throw exception;
        } finally {
            String path = normalizePath(httpRequest.getRequestURI(), httpRequest.getContextPath());
            String status = Integer.toString(requestFailed ? HttpServletResponse.SC_INTERNAL_SERVER_ERROR : httpResponse.getStatus());
            double elapsedSeconds = (System.nanoTime() - startedAt) / 1_000_000_000.0;

            HTTP_REQUESTS.labelValues(httpRequest.getMethod(), path, status).inc();
            HTTP_REQUEST_DURATION.labelValues(httpRequest.getMethod(), path, status).observe(elapsedSeconds);
        }
    }

    private String normalizePath(String requestUri, String contextPath) {
        String path = requestUri.substring(contextPath.length());
        path = UUID_IDENTIFIER.matcher(path).replaceAll("/{id}");
        return NUMERIC_IDENTIFIER.matcher(path).replaceAll("/{id}");
    }
}
