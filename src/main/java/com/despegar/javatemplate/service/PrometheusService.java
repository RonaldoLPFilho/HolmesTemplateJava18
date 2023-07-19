package com.despegar.javatemplate.service;

import com.despegar.javatemplate.model.metric.BusinessCounter;
import com.despegar.javatemplate.model.metric.BusinessLabel;
import io.micrometer.core.instrument.Tag;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PrometheusService {

    private final PrometheusMeterRegistry registry;

    @Autowired
    public PrometheusService(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    public void count(BusinessCounter counter) {
        count(counter, Map.of(), 1);
    }

    public void count(BusinessCounter counter, Integer incrementAmount) {
        count(counter, Map.of(), incrementAmount);
    }

    public void count(BusinessCounter counter, Map<BusinessLabel, String> tags) {
        count(counter, tags, 1);
    }

    public void count(BusinessCounter counter, Map<BusinessLabel, String> tags, Integer incrementAmount) {
        final var parsedTags = tags.entrySet().stream().map(tag -> Tag.of(tag.getKey().getName(), tag.getValue()))
                .collect(Collectors.toList());
        registry.counter(counter.getName(), parsedTags).increment(incrementAmount);
    }

}
