package com.despegar.javatemplate.config.server.model;

import com.despegar.javatemplate.config.thread.model.ThreadPoolExecutorProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "server.jetty.thread-pool", ignoreInvalidFields = true)
public record JettyThreadPoolProperties(ThreadPoolExecutorProperties executor, JettyQueuedThreadPoolProperties queued) {}
