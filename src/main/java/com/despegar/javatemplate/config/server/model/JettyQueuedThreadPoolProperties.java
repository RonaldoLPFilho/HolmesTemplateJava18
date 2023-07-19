package com.despegar.javatemplate.config.server.model;

import com.despegar.javatemplate.config.thread.model.BlockingQueueProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "server.jetty.thread-pool.queued", ignoreInvalidFields = true)
public record JettyQueuedThreadPoolProperties(String name,
                                              int maxThreads,
                                              int minThreads,
                                              int idleTimeout,
                                              BlockingQueueProperties queue) {}
