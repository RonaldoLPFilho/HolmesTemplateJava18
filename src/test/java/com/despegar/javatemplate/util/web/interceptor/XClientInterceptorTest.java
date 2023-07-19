package com.despegar.javatemplate.util.web.interceptor;

import com.despegar.javatemplate.model.api.error.ApiHeaderMissingException;
import com.despegar.javatemplate.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class XClientInterceptorTest {

    private static final String CLIENT = "theClient";

    @Mock
    private HttpServletRequest requestMock;

    @Mock
    private HttpServletResponse responseMock;

    @Mock
    private HandlerMethod handlerMethodMock;

    @BeforeEach
    void init() {
        openMocks(this);
    }

    @Test
    void preHandle_validationEnabledWithHeaderOk() {
        final var interceptor = new XClientInterceptor(true, List.of());

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(CLIENT);

        final var result = interceptor.preHandle(requestMock, responseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, only()).getHeader(Constants.X_CLIENT);
    }

    @Test
    void preHandle_validationEnabledWithMissingHeader() {
        final var interceptor = new XClientInterceptor(true, List.of());

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(null);

        assertThrows(ApiHeaderMissingException.class,
                () -> interceptor.preHandle(requestMock, responseMock, handlerMethodMock));

        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, only()).getHeader(Constants.X_CLIENT);
    }

    @Test
    void preHandle_validationDisabledWithHeaderOk() {
        final var interceptor = new XClientInterceptor(false, List.of());

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(CLIENT);

        final var result = interceptor.preHandle(requestMock, responseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, only()).getHeader(Constants.X_CLIENT);
    }

    @Test
    void preHandle_validationDisabledWithMissingHeader() {
        final var interceptor = new XClientInterceptor(false, List.of());

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(null);

        final var result = interceptor.preHandle(requestMock, responseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, only()).getHeader(Constants.X_CLIENT);
    }

    @Test
    void preHandle_validationEnabledMissingHeaderButPathIgnored() {
        final var path = "/path";
        final var interceptor = new XClientInterceptor(true, List.of(path, "/other"));

        when(requestMock.getHeader(Constants.X_CLIENT)).thenReturn(null);
        when(requestMock.getRequestURI()).thenReturn(path);

        final var result = interceptor.preHandle(requestMock, responseMock, handlerMethodMock);

        assertTrue(result);
        verifyNoInteractions(responseMock);
        verifyNoInteractions(handlerMethodMock);
        verify(requestMock, times(1)).getHeader(Constants.X_CLIENT);
        verify(requestMock, times(1)).getRequestURI();
        verifyNoMoreInteractions(responseMock);
    }
}
