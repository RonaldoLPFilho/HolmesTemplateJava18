package com.despegar.javatemplate.config.connector.model;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

class ConnectorDefaults {
    static final boolean SECURE = false;
    static final int MAX_CONNECTIONS = 20;
    static final Duration CONNECTION_TIMEOUT = Duration.ofMillis(500);
    static final Duration READ_TIMEOUT = Duration.of(30, ChronoUnit.SECONDS);
    static final Duration IDLE_CONNECTION_TIMEOUT = Duration.of(60, ChronoUnit.SECONDS);
    static final Duration VALIDATE_AFTER_INACTIVITY = Duration.ofMillis(-1); // off
    static final boolean CACHE_ENABLED = false;
    static final int CACHE_SIZE = 1000;
    static final int CACHE_CONCURRENCY_LEVEL = 50;
    static final List<String> CACHE_SUPPORTED_METHODS = List.of("MAX_AGE", "EXPIRES", "ETAG", "LAST_MODIFIED");
    static final CacheProperties CACHE_PROPERTIES = new CacheProperties(CACHE_ENABLED, CACHE_SIZE, CACHE_CONCURRENCY_LEVEL, CACHE_SUPPORTED_METHODS);
    static final int REQUEST_MAX_RETRIES = 1;
    static final RequestProperties REQUEST_PROPERTIES = new RequestProperties(ConnectorDefaults.REQUEST_MAX_RETRIES);
    static final boolean SHUTDOWN_HOOK_ENABLED = false;
    static final JsonFormat JSON_PROPERTIES_FORMAT = JsonFormat.SNAKE_CASE;
    static final JsonProperties JSON_PROPERTIES = JsonProperties.of(JSON_PROPERTIES_FORMAT);
}
