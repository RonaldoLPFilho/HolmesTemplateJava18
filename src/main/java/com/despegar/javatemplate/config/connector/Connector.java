package com.despegar.javatemplate.config.connector;

import com.despegar.javatemplate.config.connector.model.ClientResourceProperties;
import com.despegar.javatemplate.exception.ConnectorException;
import com.despegar.library.rest.*;
import com.despegar.library.rest.builder.HttpRequestBuilder;
import com.despegar.library.rest.compression.ContentEncoding;
import com.despegar.library.rest.utils.TypeReference;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;

import static com.despegar.library.rest.HttpStatus.OK;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;

public record Connector(String name, RestConnector restConnector,
                        Map<String, ClientResourceProperties> resourcesByName) {

    public RequestBuilder get(String path) {
        return createRequestBuilder(HttpMethod.GET, path);
    }

    public RequestBuilder post(String path) {
        return createRequestBuilder(HttpMethod.POST, path);
    }

    public RequestBuilder put(String path) {
        return createRequestBuilder(HttpMethod.PUT, path);
    }

    public RequestBuilder patch(String path) {
        return createRequestBuilder(HttpMethod.PATCH, path);
    }

    public RequestBuilder delete(String path) {
        return createRequestBuilder(HttpMethod.DELETE, path);
    }

    public RequestBuilder ofResource(String resourceName) {
        var resource = ofNullable(resourcesByName.get(resourceName))
                .orElseThrow(() -> new ConnectorException("Connector resource " + resourceName + " is empty"));

        var builder = new RequestBuilder(name, restConnector, resource.method(), resource.path());

        ofNullable(resource.mediaType()).ifPresent(builder::mediaType);
        ofNullable(resource.encoding()).ifPresent(builder::encoding);
        ofNullable(resource.headers()).ifPresent(builder::headers);
        ofNullable(resource.params()).ifPresent(builder::params);

        return builder;
    }

    private RequestBuilder createRequestBuilder(HttpMethod method, String path) {
        return new RequestBuilder(name, restConnector, method, path);
    }

    public static class RequestBuilder {
        private final String name;
        private final RestConnector restConnector;
        private final HttpMethod method;
        private final String path;

        private ContentEncoding encoding;
        private String mediaType;
        private Map<String, String> headers;
        private Map<String, String> params;
        private Object body;

        public RequestBuilder(String name,
                              RestConnector restConnector,
                              HttpMethod method,
                              String path) {
            this.name = name;
            this.restConnector = restConnector;
            this.method = method;
            this.path = path;
        }

        public RequestBuilder encoding(ContentEncoding encoding) {
            this.encoding = encoding;
            return this;
        }

        public RequestBuilder mediaType(String mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public RequestBuilder param(String key, String value) {
            if (this.params == null) this.params = new HashMap<>();
            this.params.put(key, value);
            return this;
        }

        public RequestBuilder params(Map<String, String> params) {
            if (this.params == null) this.params = new HashMap<>();
            this.params.putAll(params);
            return this;
        }

        public RequestBuilder header(String key, String value) {
            if (this.headers == null) this.headers = new HashMap<>();
            this.headers.put(key, value);
            return this;
        }

        public RequestBuilder headers(Map<String, String> headers) {
            if (this.headers == null) this.headers = new HashMap<>();
            this.headers.putAll(headers);
            return this;
        }

        public RequestBuilder body(Object body) {
            this.body = body;
            return this;
        }

        public <T> T execute(Class<T> responseType) {
            return execute(responseType, OK);
        }

        public <T> T execute(TypeReference<T> responseType) {
            return execute(responseType, OK);
        }

        public <T> T execute(Class<T> responseType, HttpStatus... expectedStatus) {
            TypeReference<T> typeReference = new TypeReference<>() {
                @Override
                public Type getType() {
                    return responseType;
                }
            };
            return execute(typeReference, expectedStatus);
        }

        public <T> T execute(TypeReference<T> responseType, HttpStatus... expectedStatus) {
            try {
                var response = execute();

                if (Arrays.asList(expectedStatus).contains(response.getStatus()))
                    return response.getBodyAs(responseType);

                throw new ConnectorException("Status " + response.getStatus().getCode() + ". " + response.getBody());
            } catch (IOException e) {
                throw new ConnectorException("Error on http " + method, e);
            }
        }

        public HttpResponse execute() {
            var url = buildURL(path, Optional.ofNullable(params).orElse(Collections.emptyMap()));
            var builder = initHttpRequestBuilder(url);

            if (encoding != null)
                builder = builder.acceptEncoding(encoding);

            if (encoding != null && body != null)
                builder = builder.encodeWith(encoding);

            if (mediaType != null)
                builder = builder.accept(mediaType).asContentType(mediaType);

            if (body != null)
                builder = builder.withBody(body);

            try {
                return builder
                        .withHeaders(toHttpHeaders.apply(headers))
                        .execute();
            } catch (IOException e) {
                throw new ConnectorException("Error on http " + method + ": " + url, e);
            }
        }

        private HttpRequestBuilder initHttpRequestBuilder(String url) {
            return switch (method) {
                case GET -> restConnector.get(url);
                case POST -> restConnector.post(url);
                case PUT -> restConnector.put(url);
                case DELETE -> restConnector.delete(url);
                case PATCH -> restConnector.patch(url);
                default -> throw new RuntimeException("Unsupported HTTP method: " + method);
            };
        }

        private String buildURL(String path, Map<String, String> params) {
            return path + queryString(params);
        }

        private String queryString(Map<String, String> queryArgs) {
            String query = queryArgs.entrySet().stream()
                    .filter(entry -> !entry.getKey().isEmpty())
                    .filter(entry -> !entry.getValue().isEmpty())
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(joining("&"));

            return query.isEmpty() ? "" : "?" + query;
        }

        private static final Function<Map<String, String>, HttpHeaders> toHttpHeaders = (headers) -> {
            HttpHeaders httpHeaders = new HttpHeaders();
            ofNullable(headers).ifPresent(h -> httpHeaders.setAll(headers));
            return httpHeaders;
        };
    }
}
