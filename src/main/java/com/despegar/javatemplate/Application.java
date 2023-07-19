package com.despegar.javatemplate;

import com.despegar.javatemplate.util.RecordNamingStrategyPatchModule;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@ConfigurationPropertiesScan("com.despegar.javatemplate")

// Local
@PropertySource(value = "classpath:environment.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:sensitive.conf", encoding = "UTF-8", ignoreResourceNotFound = true)

// CloudIA
@PropertySource(value = "file:/home/despegar/app/config/environment.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
@PropertySource(value = "file:/home/despegar/sensitive.conf", encoding = "UTF-8", ignoreResourceNotFound = true)

// Nebula contract
@PropertySource(value = "file:/service/config/environment.properties", encoding = "UTF-8", ignoreResourceNotFound = true)
@PropertySource(value = "file:/service/secrets/sensitive.conf", encoding = "UTF-8", ignoreResourceNotFound = true)

public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*
     * Permite deserializar los request a la app con SNAKE_CASE ya que se agrega
     * al mapper que usa spring para los request.
     */
    @Bean
    public RecordNamingStrategyPatchModule recordNamingStrategyPatchModule() {
        return new RecordNamingStrategyPatchModule();
    }
}
