package com.despegar.javatemplate.util.web.filter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.Appender;
import com.despegar.javatemplate.util.Constants;
import com.despegar.library.routing.RSD;
import com.google.common.collect.Iterators;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

public class AccessFilterTest {

    private static final String IGNORED_PATH = "/ignored";
    private static final String REFERER = "referer";
    private static final String X_FORWARDED_FOR = "x-forwarded-for";
    private static final String X_CLIENT = "client";
    private static final Appender<ILoggingEvent> MOCKED_APPENDER = (Appender<ILoggingEvent>) Mockito.mock(Appender.class);

    @Captor
    private ArgumentCaptor<LoggingEvent> loggingEventCaptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private ServletResponse response;

    @Mock
    private FilterChain filterChain;

    private final AccessFilter accessFilter = new AccessFilter(List.of(IGNORED_PATH));

    @BeforeAll
    static void setup() {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.addAppender(MOCKED_APPENDER);
        root.setLevel(Level.INFO);
    }

    @BeforeEach
    void init() {
        openMocks(this);
        accessFilter.init(null);
        reset(MOCKED_APPENDER);
        RSD.clear();
    }

    @AfterEach
    void destroy(){
        accessFilter.destroy();
    }

    @Test
    void doFilter_notShouldLog() throws IOException, ServletException {
        when(request.getHeader(Constants.REFERER)).thenReturn(REFERER);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(X_CLIENT);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(X_FORWARDED_FOR);
        when(request.getRequestURI()).thenReturn(IGNORED_PATH);

        accessFilter.doFilter(request, response, filterChain);

        assertEquals(REFERER, RSD.get(Constants.REFERER));
        assertEquals(X_CLIENT, RSD.get(Constants.X_CLIENT));
        assertEquals(X_FORWARDED_FOR, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);
        verifyNoInteractions(MOCKED_APPENDER);
        assertTrue(loggingEventCaptor.getAllValues().isEmpty());
    }

    @Test
    void doFilter_defaults() throws IOException, ServletException {
        final var addr = "addr";

        when(request.getHeader(Constants.REFERER)).thenReturn(null);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(null);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(null);
        when(request.getRequestURI()).thenReturn(IGNORED_PATH);
        when(request.getRemoteAddr()).thenReturn(addr);

        accessFilter.doFilter(request, response, filterChain);

        assertEquals(Constants.UNKNOWN, RSD.get(Constants.REFERER));
        assertEquals(Constants.UNKNOWN, RSD.get(Constants.X_CLIENT));
        assertEquals(addr, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);
        verifyNoInteractions(MOCKED_APPENDER);
        assertTrue(loggingEventCaptor.getAllValues().isEmpty());
    }

    @Test
    void doFilter_shouldLog() throws IOException, ServletException {
        when(request.getHeader(Constants.REFERER)).thenReturn(REFERER);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(X_CLIENT);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(X_FORWARDED_FOR);
        when(request.getRequestURI()).thenReturn("/should-log");

        final var urlStringBuffer = mock(StringBuffer.class);
        final var url = "url";
        final var queryString = "queryString";
        final var method = "method";
        final var headerKey1 = "header1";
        final var headerValue1 = "headerValue1";
        final var headerKey2 = "header2";
        final var headerValue2 = "headerValue2";

        when(request.getRequestURL()).thenReturn(urlStringBuffer);
        when(urlStringBuffer.toString()).thenReturn(url);
        when(request.getQueryString()).thenReturn(queryString);
        when(request.getMethod()).thenReturn(method);
        when(request.getHeaderNames())
                .thenReturn(Iterators.asEnumeration(List.of(headerKey1, headerKey2).iterator()));
        when(request.getHeader(headerKey1)).thenReturn(headerValue1);
        when(request.getHeader(headerKey2)).thenReturn(headerValue2);

        accessFilter.doFilter(request, response, filterChain);

        assertEquals(REFERER, RSD.get(Constants.REFERER));
        assertEquals(X_CLIENT, RSD.get(Constants.X_CLIENT));
        assertEquals(X_FORWARDED_FOR, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getRequestURL();
        verify(request, times(1)).getQueryString();
        verify(request, times(1)).getMethod();
        verify(request, times(1)).getHeaderNames();
        verify(request, times(1)).getHeader(headerKey1);
        verify(request, times(1)).getHeader(headerKey2);
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);

        verify(MOCKED_APPENDER, only()).doAppend(loggingEventCaptor.capture());
        final var logEvents = loggingEventCaptor.getAllValues();
        assertEquals(1, logEvents.size());
        final var expectedHeadersLog = "Headers: %s=%s|%s=%s|".formatted(headerKey1, headerValue1, headerKey2, headerValue2);
        final var expectedLogLine = "Request -> %s -> %s %s -> Querystring: %s".formatted(method,
                url, expectedHeadersLog, queryString);
        assertEquals(expectedLogLine, logEvents.get(0).getFormattedMessage());
    }

    @Test
    void doFilter_onException() throws IOException, ServletException {
        when(request.getHeader(Constants.REFERER)).thenReturn(REFERER);
        when(request.getHeader(Constants.X_CLIENT)).thenReturn(X_CLIENT);
        when(request.getHeader(Constants.X_FORWARDED_FOR)).thenReturn(X_FORWARDED_FOR);
        when(request.getRequestURI()).thenReturn("/should-log");

        final var urlStringBuffer = mock(StringBuffer.class);
        final var url = "url";
        final var method = "method";
        final var headerKey1 = "header1";
        final var headerValue1 = "headerValue1";
        final var headerKey2 = "header2";
        final var headerValue2 = "headerValue2";
        final var headerKey3 = "Authorization";
        final var headerValue3 = "headerValue3";

        when(request.getRequestURL()).thenReturn(urlStringBuffer);
        when(urlStringBuffer.toString()).thenReturn(url);
        when(request.getQueryString()).thenThrow(UncheckedIOException.class);
        when(request.getMethod()).thenReturn(method);
        when(request.getHeaderNames())
                .thenReturn(Iterators.asEnumeration(List.of(headerKey1, headerKey2, headerKey3).iterator()));
        when(request.getHeader(headerKey1)).thenReturn(headerValue1);
        when(request.getHeader(headerKey2)).thenReturn(headerValue2);
        when(request.getHeader(headerKey3)).thenReturn(headerValue3);

        accessFilter.doFilter(request, response, filterChain);

        assertEquals(REFERER, RSD.get(Constants.REFERER));
        assertEquals(X_CLIENT, RSD.get(Constants.X_CLIENT));
        assertEquals(X_FORWARDED_FOR, RSD.get(Constants.X_FORWARDED_FOR));
        verify(request, times(1)).getHeader(Constants.REFERER);
        verify(request, times(1)).getHeader(Constants.X_CLIENT);
        verify(request, times(1)).getHeader(Constants.X_FORWARDED_FOR);
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getRequestURI();
        verify(request, times(1)).getRequestURL();
        verify(request, times(1)).getQueryString();
        verifyNoInteractions(response);
        verify(filterChain, only()).doFilter(request, response);

        verify(MOCKED_APPENDER, only()).doAppend(loggingEventCaptor.capture());
        final var logEvents = loggingEventCaptor.getAllValues();
        assertEquals(1, logEvents.size());
        assertEquals("Error trying log initial request info. ", logEvents.get(0).getFormattedMessage());
    }

}
