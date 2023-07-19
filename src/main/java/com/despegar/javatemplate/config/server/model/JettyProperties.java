package com.despegar.javatemplate.config.server.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "server.jetty", ignoreInvalidFields = true)
public record JettyProperties(Integer port, String contextPath, JettyThreadPoolProperties threadPool) {}
