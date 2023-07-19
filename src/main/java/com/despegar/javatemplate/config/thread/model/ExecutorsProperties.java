package com.despegar.javatemplate.config.thread.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.util.Map;

@ConstructorBinding
@ConfigurationProperties(prefix = "thread-pool", ignoreInvalidFields = true)
public record ExecutorsProperties(Map<String, ThreadPoolExecutorProperties> executors) {}
