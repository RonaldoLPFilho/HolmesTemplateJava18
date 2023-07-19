package com.despegar.javatemplate.util.web.filter;

import com.despegar.javatemplate.util.Constants;
import com.despegar.library.routing.RSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AccessFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessFilter.class);

    private final List<String> ignoredPaths;

    public AccessFilter(List<String> ignoredPaths) {
        this.ignoredPaths = ignoredPaths;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        final var httpRequest = (HttpServletRequest) request;

        saveInContextHeaderOrDefault(httpRequest, Constants.REFERER, Constants.UNKNOWN);
        saveInContextHeaderOrDefault(httpRequest, Constants.X_CLIENT, Constants.UNKNOWN);
        saveInContextHeaderOrDefault(httpRequest, Constants.X_FORWARDED_FOR, httpRequest.getRemoteAddr());

        if(shouldLog(httpRequest)) {
            logRequestInitialInfo(httpRequest);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig arg0) {
        LOGGER.info("Initializing AccessLogFilter");
    }

    @Override
    public void destroy() {
        LOGGER.info("Destroying AccessLogFilter");
    }

    private void saveInContextHeaderOrDefault(HttpServletRequest request, String headerKey, String defaultValue) {
        final var headerValue = Optional.ofNullable(request.getHeader(headerKey)).orElse(defaultValue);
        RSD.put(headerKey, headerValue);
    }

    private boolean shouldLog(HttpServletRequest request) {
        return ignoredPaths.stream()
                .noneMatch(ignoredPath -> request.getRequestURI().toLowerCase().endsWith(ignoredPath));
    }

    private void logRequestInitialInfo(HttpServletRequest request) {
        try {
            final var requestUrl = request.getRequestURL().toString();
            final var queryString = request.getQueryString();
            final var method = request.getMethod();
            final var headers = extractHeaders(request);

            LOGGER.info("Request -> {} -> {} {} -> Querystring: {}", method, requestUrl, headers,
                    queryString);

        } catch (Exception ex) {
            LOGGER.warn("Error trying log initial request info. ", ex);
        }
    }

    private String extractHeaders(HttpServletRequest request) {
        final var builder = new StringBuilder("Headers: ");

        Collections.list(request.getHeaderNames()).stream()
                .filter(name -> !isAuthorizationHeader(name))
                .forEach(headerName -> builder.append("%s=%s|".formatted(headerName, request.getHeader(headerName))));

        return builder.toString();
    }

    private boolean isAuthorizationHeader(String headerName) {
        return Constants.AUTHORIZATION_HEADER.equalsIgnoreCase(headerName);
    }

}
