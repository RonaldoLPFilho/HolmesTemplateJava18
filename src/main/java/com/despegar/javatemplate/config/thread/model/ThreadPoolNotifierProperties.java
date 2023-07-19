package com.despegar.javatemplate.config.thread.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "thread-pool.notifier")
public record ThreadPoolNotifierProperties(Integer scheduleInSeconds) {}
