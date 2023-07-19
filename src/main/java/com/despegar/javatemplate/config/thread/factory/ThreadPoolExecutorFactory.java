package com.despegar.javatemplate.config.thread.factory;

import com.despegar.javatemplate.config.thread.model.ThreadPoolExecutorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class ThreadPoolExecutorFactory {
    private final BlockingQueueFactory blockingQueueFactory;

    @Autowired
    public ThreadPoolExecutorFactory(BlockingQueueFactory blockingQueueFactory) {
        this.blockingQueueFactory = blockingQueueFactory;
    }

    public ThreadPoolExecutor create(ThreadPoolExecutorProperties properties) {
        return new ThreadPoolExecutor(
                properties.corePoolSize(),
                properties.maximumPoolSize(),
                properties.keepAliveTime(),
                TimeUnit.MILLISECONDS,
                blockingQueueFactory.create(properties.queue()),
                new CustomizableThreadFactory(properties.name()));
    }
}
