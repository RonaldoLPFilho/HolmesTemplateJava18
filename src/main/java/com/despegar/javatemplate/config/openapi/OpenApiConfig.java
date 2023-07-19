package com.despegar.javatemplate.config.openapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${app.version}") String version, ApiDocProperties apiDocProperties) {
        return new OpenAPI()
                .components(new Components())
                .info(new Info()
                        .title(apiDocProperties.title())
                        .description(apiDocProperties.description())
                        .version(version)
                        .contact(new Contact()
                                .name(apiDocProperties.contact().name())
                                .email(apiDocProperties.contact().email())
                                .url(apiDocProperties.contact().url()))
                );
    }

    @Bean
    public ModelResolver modelResolverWithSpringObjectMapper(ObjectMapper objectMapper) {
        return new ModelResolver(objectMapper);
    }

    @ConstructorBinding
    @ConfigurationProperties(prefix = "app.api-doc")
    public record ApiDocProperties(String title, String description, ApiDocContact contact) {
    }

    public record ApiDocContact(String name, String email, String url) {
    }
}
