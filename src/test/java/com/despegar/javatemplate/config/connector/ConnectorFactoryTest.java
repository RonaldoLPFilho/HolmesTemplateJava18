package com.despegar.javatemplate.config.connector;

import com.despegar.javatemplate.config.connector.model.*;
import com.despegar.library.rest.interceptors.Interceptor;
import com.despegar.library.rest.interceptors.Interceptors;
import com.despegar.library.rest.interceptors.impl.CacheInterceptor;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.mockito.MockitoAnnotations.openMocks;

public class ConnectorFactoryTest {

    private final ConnectorFactory target = new ConnectorFactory();

    @Mock
    private RestConnectorProperties restConnectorPropertiesMock;
    @Mock
    private RequestProperties requestPropertiesMock;
    @Mock
    private CacheProperties cachePropertiesMock;
    @Mock
    private ClientResourceProperties clientResourcePropertiesMock;
    private final JsonProperties jsonProperties = JsonProperties.of(JsonFormat.SNAKE_CASE);

    @BeforeEach
    void init() {
        openMocks(this);
    }

    @Test
    public void createTest_ok(){
        var resources = Maps.newHashMap(Collections.singletonMap("exampleTag",clientResourcePropertiesMock));
        var connectorProperties = new MockConnectorProperties(restConnectorPropertiesMock, resources);

        Mockito.when(clientResourcePropertiesMock.name()).thenReturn("name");
        Mockito.when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        Mockito.when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(true);
        Mockito.when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        Mockito.when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        Mockito.when(requestPropertiesMock.maxRetries()).thenReturn(7);
        Mockito.when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        Mockito.when(cachePropertiesMock.enabled()).thenReturn(true);
        Mockito.when(cachePropertiesMock.size()).thenReturn(10);
        Mockito.when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        Mockito.when(cachePropertiesMock.supportedMethods()).thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        Mockito.when(restConnectorPropertiesMock.secure()).thenReturn(true);
        Mockito.when(restConnectorPropertiesMock.host()).thenReturn("host");
        Mockito.when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        Mockito.when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        Mockito.when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        var connector = this.target.create("geo",connectorProperties);

        Assertions.assertEquals("https://host",connector.restConnector().getEndpoint());
        Assertions.assertEquals("geo",connector.name());
        Assertions.assertEquals(clientResourcePropertiesMock,connector.resourcesByName().get("name"));
        Assertions.assertNotNull(connector.restConnector().getSerializers());
        Assertions.assertNotNull(connector.restConnector().getStreamSerializers());
        Assertions.assertEquals(3,connector.restConnector().getInterceptors().size());

        Mockito.verify(restConnectorPropertiesMock,Mockito.times(2)).secure();
        Mockito.verify(restConnectorPropertiesMock).host();
        Mockito.verify(restConnectorPropertiesMock).endpoint();
        Mockito.verify(restConnectorPropertiesMock).shutdownHookEnabled();
        Mockito.verify(restConnectorPropertiesMock).maxConnections();
        Mockito.verify(restConnectorPropertiesMock).connectionTimeout();
        Mockito.verify(restConnectorPropertiesMock).request();
        Mockito.verify(requestPropertiesMock).maxRetries();
        Mockito.verify(restConnectorPropertiesMock).validateAfterInactivity();
        Mockito.verify(restConnectorPropertiesMock,Mockito.times(4)).cache();
        Mockito.verify(cachePropertiesMock).enabled();
        Mockito.verify(cachePropertiesMock).concurrencyLevel();
        Mockito.verify(cachePropertiesMock).size();
        Mockito.verify(cachePropertiesMock).supportedMethods();
        Mockito.verify(restConnectorPropertiesMock,Mockito.times(2)).json();
    }

    @Test
    public void createWithInterceptorTest(){
        Interceptor interceptor = Mockito.mock(Interceptor.class);

        var resources = Maps.newHashMap(Collections.singletonMap("exampleTag",clientResourcePropertiesMock));
        var connectorProperties = new MockConnectorProperties(restConnectorPropertiesMock, resources);

        Mockito.when(clientResourcePropertiesMock.name()).thenReturn("name");
        Mockito.when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        Mockito.when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(true);
        Mockito.when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        Mockito.when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        Mockito.when(requestPropertiesMock.maxRetries()).thenReturn(7);
        Mockito.when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        Mockito.when(cachePropertiesMock.enabled()).thenReturn(true);
        Mockito.when(cachePropertiesMock.size()).thenReturn(10);
        Mockito.when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        Mockito.when(cachePropertiesMock.supportedMethods()).thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        Mockito.when(restConnectorPropertiesMock.secure()).thenReturn(true);
        Mockito.when(restConnectorPropertiesMock.host()).thenReturn("host");
        Mockito.when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        Mockito.when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        Mockito.when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        var connector = this.target.create("geo",connectorProperties, List.of(interceptor));

        Assertions.assertEquals("https://host", connector.restConnector().getEndpoint());
        Assertions.assertEquals("geo", connector.name());
        Assertions.assertEquals(clientResourcePropertiesMock, connector.resourcesByName().get("name"));
        Assertions.assertNotNull(connector.restConnector().getSerializers());
        Assertions.assertNotNull(connector.restConnector().getStreamSerializers());
        // Tiene que haber 4 interceptors: AcceptJsonInterceptor, CacheInterceptor, RoutingInterceptor y el del test.
        Assertions.assertEquals(4, connector.restConnector().getInterceptors().size());
        Interceptor lastInterceptor = getLastInterceptor(connector.restConnector().getInterceptors());
        Assertions.assertSame(lastInterceptor, interceptor);

        Mockito.verify(restConnectorPropertiesMock, Mockito.times(2)).secure();
        Mockito.verify(restConnectorPropertiesMock).host();
        Mockito.verify(restConnectorPropertiesMock).endpoint();
        Mockito.verify(restConnectorPropertiesMock).shutdownHookEnabled();
        Mockito.verify(restConnectorPropertiesMock).maxConnections();
        Mockito.verify(restConnectorPropertiesMock).connectionTimeout();
        Mockito.verify(restConnectorPropertiesMock).request();
        Mockito.verify(requestPropertiesMock).maxRetries();
        Mockito.verify(restConnectorPropertiesMock).validateAfterInactivity();
        Mockito.verify(restConnectorPropertiesMock, Mockito.times(4)).cache();
        Mockito.verify(cachePropertiesMock).enabled();
        Mockito.verify(cachePropertiesMock).concurrencyLevel();
        Mockito.verify(cachePropertiesMock).size();
        Mockito.verify(cachePropertiesMock).supportedMethods();
        Mockito.verify(restConnectorPropertiesMock, Mockito.times(2)).json();
    }


    @Test
    public void createTest_cacheOff_ok(){
        var resources = Maps.newHashMap(Collections.singletonMap("exampleTag",clientResourcePropertiesMock));
        var connectorProperties = new MockConnectorProperties(restConnectorPropertiesMock, resources);

        Mockito.when(clientResourcePropertiesMock.name()).thenReturn("name");
        Mockito.when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        Mockito.when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(true);
        Mockito.when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        Mockito.when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        Mockito.when(requestPropertiesMock.maxRetries()).thenReturn(7);
        Mockito.when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        Mockito.when(cachePropertiesMock.enabled()).thenReturn(false);
        Mockito.when(cachePropertiesMock.size()).thenReturn(10);
        Mockito.when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        Mockito.when(cachePropertiesMock.supportedMethods()).thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        Mockito.when(restConnectorPropertiesMock.secure()).thenReturn(true);
        Mockito.when(restConnectorPropertiesMock.host()).thenReturn("host");
        Mockito.when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        Mockito.when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        Mockito.when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        var connector = this.target.create("geo",connectorProperties);

        Assertions.assertEquals("https://host",connector.restConnector().getEndpoint());
        Assertions.assertEquals("geo",connector.name());
        Assertions.assertEquals(clientResourcePropertiesMock,connector.resourcesByName().get("name"));
        Assertions.assertNotNull(connector.restConnector().getSerializers());
        Assertions.assertNotNull(connector.restConnector().getStreamSerializers());
        Assertions.assertEquals(2,connector.restConnector().getInterceptors().size());

        Mockito.verify(restConnectorPropertiesMock,Mockito.times(2)).secure();
        Mockito.verify(restConnectorPropertiesMock).host();
        Mockito.verify(restConnectorPropertiesMock).endpoint();
        Mockito.verify(restConnectorPropertiesMock).shutdownHookEnabled();
        Mockito.verify(restConnectorPropertiesMock).maxConnections();
        Mockito.verify(restConnectorPropertiesMock).connectionTimeout();
        Mockito.verify(restConnectorPropertiesMock).request();
        Mockito.verify(requestPropertiesMock).maxRetries();
        Mockito.verify(restConnectorPropertiesMock).validateAfterInactivity();
        Mockito.verify(restConnectorPropertiesMock).cache();
        Mockito.verify(cachePropertiesMock).enabled();
        Mockito.verify(cachePropertiesMock,Mockito.times(0)).concurrencyLevel();
        Mockito.verify(cachePropertiesMock,Mockito.times(0)).size();
        Mockito.verify(cachePropertiesMock,Mockito.times(0)).supportedMethods();
        Mockito.verify(restConnectorPropertiesMock,Mockito.times(2)).json();
    }

    @Test
    public void createTest_notSecure_ok(){
        var resources = Maps.newHashMap(Collections.singletonMap("exampleTag",clientResourcePropertiesMock));
        var connectorProperties = new MockConnectorProperties(restConnectorPropertiesMock, resources);

        Mockito.when(clientResourcePropertiesMock.name()).thenReturn("name");
        Mockito.when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        Mockito.when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(true);
        Mockito.when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        Mockito.when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        Mockito.when(requestPropertiesMock.maxRetries()).thenReturn(7);
        Mockito.when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        Mockito.when(cachePropertiesMock.enabled()).thenReturn(true);
        Mockito.when(cachePropertiesMock.size()).thenReturn(10);
        Mockito.when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        Mockito.when(cachePropertiesMock.supportedMethods()).thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        Mockito.when(restConnectorPropertiesMock.secure()).thenReturn(false);
        Mockito.when(restConnectorPropertiesMock.host()).thenReturn("host");
        Mockito.when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        Mockito.when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        Mockito.when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        var connector = this.target.create("geo",connectorProperties);

        Assertions.assertEquals("http://host",connector.restConnector().getEndpoint());
        Assertions.assertEquals("geo",connector.name());
        Assertions.assertEquals(clientResourcePropertiesMock,connector.resourcesByName().get("name"));
        Assertions.assertNotNull(connector.restConnector().getSerializers());
        Assertions.assertNotNull(connector.restConnector().getStreamSerializers());
        Assertions.assertEquals(3,connector.restConnector().getInterceptors().size());

        Mockito.verify(restConnectorPropertiesMock,Mockito.times(2)).secure();
        Mockito.verify(restConnectorPropertiesMock).host();
        Mockito.verify(restConnectorPropertiesMock).endpoint();
        Mockito.verify(restConnectorPropertiesMock).shutdownHookEnabled();
        Mockito.verify(restConnectorPropertiesMock).maxConnections();
        Mockito.verify(restConnectorPropertiesMock).connectionTimeout();
        Mockito.verify(restConnectorPropertiesMock).request();
        Mockito.verify(requestPropertiesMock).maxRetries();
        Mockito.verify(restConnectorPropertiesMock).validateAfterInactivity();
        Mockito.verify(restConnectorPropertiesMock,Mockito.times(4)).cache();
        Mockito.verify(cachePropertiesMock).enabled();
        Mockito.verify(cachePropertiesMock).concurrencyLevel();
        Mockito.verify(cachePropertiesMock).size();
        Mockito.verify(cachePropertiesMock).supportedMethods();
        Mockito.verify(restConnectorPropertiesMock,Mockito.times(2)).json();
    }


    @Test
    public void createTest_shutdownHookNotEnabled_ok(){
        var resources = Maps.newHashMap(Collections.singletonMap("exampleTag",clientResourcePropertiesMock));
        var connectorProperties = new MockConnectorProperties(restConnectorPropertiesMock, resources);

        Mockito.when(clientResourcePropertiesMock.name()).thenReturn("name");
        Mockito.when(restConnectorPropertiesMock.endpoint()).thenReturn("endpoint-app");
        Mockito.when(restConnectorPropertiesMock.shutdownHookEnabled()).thenReturn(false);
        Mockito.when(restConnectorPropertiesMock.maxConnections()).thenReturn(20);
        Mockito.when(restConnectorPropertiesMock.connectionTimeout()).thenReturn(Duration.of(200, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.readTimeout()).thenReturn(Duration.of(300, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.idleConnectionTimeout()).thenReturn(Duration.of(400, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.request()).thenReturn(requestPropertiesMock);
        Mockito.when(requestPropertiesMock.maxRetries()).thenReturn(7);
        Mockito.when(restConnectorPropertiesMock.validateAfterInactivity()).thenReturn(Duration.of(33, ChronoUnit.SECONDS));
        Mockito.when(restConnectorPropertiesMock.cache()).thenReturn(cachePropertiesMock);
        Mockito.when(cachePropertiesMock.enabled()).thenReturn(true);
        Mockito.when(cachePropertiesMock.size()).thenReturn(10);
        Mockito.when(cachePropertiesMock.concurrencyLevel()).thenReturn(20);
        Mockito.when(cachePropertiesMock.supportedMethods()).thenReturn(Lists.newArrayList(CacheInterceptor.CacheMethods.MAX_AGE.name()));
        Mockito.when(restConnectorPropertiesMock.secure()).thenReturn(true);
        Mockito.when(restConnectorPropertiesMock.host()).thenReturn("host");
        Mockito.when(restConnectorPropertiesMock.json()).thenReturn(jsonProperties);
        Mockito.when(restConnectorPropertiesMock.clientId()).thenReturn("clientId");
        Mockito.when(restConnectorPropertiesMock.xVersion()).thenReturn("xVersion");

        var connector = this.target.create("geo",connectorProperties);

        Assertions.assertEquals("https://host",connector.restConnector().getEndpoint());
        Assertions.assertEquals("geo",connector.name());
        Assertions.assertEquals(clientResourcePropertiesMock,connector.resourcesByName().get("name"));
        Assertions.assertNotNull(connector.restConnector().getSerializers());
        Assertions.assertNotNull(connector.restConnector().getStreamSerializers());
        Assertions.assertEquals(3,connector.restConnector().getInterceptors().size());

        Mockito.verify(restConnectorPropertiesMock,Mockito.times(2)).secure();
        Mockito.verify(restConnectorPropertiesMock).host();
        Mockito.verify(restConnectorPropertiesMock).endpoint();
        Mockito.verify(restConnectorPropertiesMock).shutdownHookEnabled();
        Mockito.verify(restConnectorPropertiesMock).maxConnections();
        Mockito.verify(restConnectorPropertiesMock).connectionTimeout();
        Mockito.verify(restConnectorPropertiesMock).request();
        Mockito.verify(requestPropertiesMock).maxRetries();
        Mockito.verify(restConnectorPropertiesMock).validateAfterInactivity();
        Mockito.verify(restConnectorPropertiesMock,Mockito.times(4)).cache();
        Mockito.verify(cachePropertiesMock).enabled();
        Mockito.verify(cachePropertiesMock).concurrencyLevel();
        Mockito.verify(cachePropertiesMock).size();
        Mockito.verify(cachePropertiesMock).supportedMethods();
        Mockito.verify(restConnectorPropertiesMock,Mockito.times(2)).json();
    }

    private static class MockConnectorProperties extends ConnectorProperties {

        public MockConnectorProperties(RestConnectorProperties connector, Map<String, ClientResourceProperties> resources) {
            super(connector, resources);
        }

    }


    private <T> T getLastInterceptor(Interceptors interceptors) {
        try {
            Field field = interceptors.getClass().getDeclaredField("lastInterceptor");
            field.setAccessible(true);
            @SuppressWarnings("unchecked") T value = (T) field.get(interceptors);
            return value;
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            return null;
        }
    }

}
