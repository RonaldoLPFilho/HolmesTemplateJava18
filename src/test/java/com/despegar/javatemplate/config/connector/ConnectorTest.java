package com.despegar.javatemplate.config.connector;

import com.despegar.javatemplate.config.connector.model.ClientResourceProperties;
import com.despegar.javatemplate.exception.ConnectorException;
import com.despegar.library.rest.HttpMethod;
import com.despegar.library.rest.HttpResponse;
import com.despegar.library.rest.RestConnector;
import com.despegar.library.rest.builder.HttpRequestBuilder;
import com.despegar.library.rest.compression.ContentEncoding;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.MockitoAnnotations.openMocks;

public class ConnectorTest {

    private Connector connector;
    @Mock
    private RestConnector restConnectorMock;
    @Mock
    private HttpRequestBuilder httpRequestBuilderMock;
    @Mock
    private HttpResponse httpResponseMock;
    @Mock
    private ClientResourceProperties clientResourcePropertiesMock;

    @BeforeEach
    public void initTarget(){
        Mockito.when(clientResourcePropertiesMock.name()).thenReturn("geo");
        Mockito.when(clientResourcePropertiesMock.method()).thenReturn(HttpMethod.GET);
        Mockito.when(clientResourcePropertiesMock.path()).thenReturn("pathGeo");
        Mockito.when(clientResourcePropertiesMock.mediaType()).thenReturn("application/json");
        Mockito.when(clientResourcePropertiesMock.encoding()).thenReturn(ContentEncoding.GZIP);
        Mockito.when(clientResourcePropertiesMock.headers()).thenReturn(Maps.newHashMap(Collections.singletonMap("headerKey1","headerValue1")));
        Mockito.when(clientResourcePropertiesMock.params()).thenReturn(Maps.newHashMap(Collections.singletonMap("param1","value1")));
        var resourcesMock = Maps.newHashMap(Collections.singletonMap("exampleTag",clientResourcePropertiesMock));
        this.connector = new Connector("geoTest",restConnectorMock,resourcesMock);
    }

    @BeforeEach
    void init() {
        openMocks(this);
    }

    @Test
    public void getExecuteTest_ok() throws IOException {
        var body = "{'jsonBody':'valueExample'}";

        Mockito.when(restConnectorMock.get(anyString())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.acceptEncoding(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.encodeWith(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.accept(anyString())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.asContentType(anyString())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.withBody(any())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.withHeaders(any())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.execute()).thenReturn(httpResponseMock);

        var requestBuilder = this.connector.ofResource("exampleTag");
        requestBuilder.body(body);
        var httpResponse = requestBuilder.execute();

        Assertions.assertEquals(httpResponseMock,httpResponse);

        Mockito.verify(httpRequestBuilderMock).acceptEncoding(ContentEncoding.GZIP);
        Mockito.verify(httpRequestBuilderMock).encodeWith(ContentEncoding.GZIP);
        Mockito.verify(httpRequestBuilderMock).accept("application/json");
        Mockito.verify(httpRequestBuilderMock).asContentType("application/json");
        Mockito.verify(httpRequestBuilderMock).withBody(body);
        Mockito.verify(httpRequestBuilderMock).withHeaders(any());
        Mockito.verify(httpRequestBuilderMock).execute();
    }

    @Test
    public void getExecuteTest_error() throws IOException {
        var body = "{'jsonBody':'valueExample'}";

        Mockito.when(restConnectorMock.get(anyString())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.acceptEncoding(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.encodeWith(any(ContentEncoding.class))).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.accept(anyString())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.asContentType(anyString())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.withBody(any())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.withHeaders(any())).thenReturn(httpRequestBuilderMock);
        Mockito.when(httpRequestBuilderMock.execute()).thenThrow(IOException.class);

        var requestBuilder = this.connector.ofResource("exampleTag");
        requestBuilder.body(body);

        Assertions.assertThrows(ConnectorException.class, () -> {
            requestBuilder.execute();
        });

        Mockito.verify(httpRequestBuilderMock).acceptEncoding(ContentEncoding.GZIP);
        Mockito.verify(httpRequestBuilderMock).encodeWith(ContentEncoding.GZIP);
        Mockito.verify(httpRequestBuilderMock).accept("application/json");
        Mockito.verify(httpRequestBuilderMock).asContentType("application/json");
        Mockito.verify(httpRequestBuilderMock).withBody(body);
        Mockito.verify(httpRequestBuilderMock).withHeaders(any());
        Mockito.verify(httpRequestBuilderMock).execute();
    }
}
