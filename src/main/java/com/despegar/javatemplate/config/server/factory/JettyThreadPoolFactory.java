package com.despegar.javatemplate.config.server.factory;

import com.despegar.javatemplate.config.server.model.JettyQueuedThreadPoolProperties;
import com.despegar.javatemplate.config.server.model.JettyThreadPoolProperties;
import com.despegar.javatemplate.config.thread.factory.BlockingQueueFactory;
import com.despegar.javatemplate.config.thread.factory.ThreadPoolExecutorFactory;
import com.despegar.javatemplate.config.thread.model.ThreadPoolExecutorProperties;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.util.Optional.ofNullable;

@Component
public class JettyThreadPoolFactory {
    private final ThreadPoolExecutorFactory threadPoolExecutorFactory;
    private final BlockingQueueFactory blockingQueueFactory;

    @Autowired
    public JettyThreadPoolFactory(ThreadPoolExecutorFactory threadPoolExecutorFactory,
                                  BlockingQueueFactory blockingQueueFactory) {
        this.threadPoolExecutorFactory = threadPoolExecutorFactory;
        this.blockingQueueFactory = blockingQueueFactory;
    }

    public ThreadPool create(JettyThreadPoolProperties threadPool) {
        var executor = ofNullable(threadPool.executor()).map(this::createExecutorThreadPool);
        var queued = ofNullable(threadPool.queued()).map(this::createQueuedThreadPool);

        return executor
                .or(() -> queued)
                .orElseThrow(() -> new UnsupportedOperationException("Failed to bind server.jetty.thread-pool properties. Update your application's configuration"));
    }

    private ThreadPool createExecutorThreadPool(ThreadPoolExecutorProperties properties) {
        return new ExecutorThreadPool(threadPoolExecutorFactory.create(properties));
    }

    private ThreadPool createQueuedThreadPool(JettyQueuedThreadPoolProperties properties) {
        var threadPool = new QueuedThreadPool(
                properties.maxThreads(),
                properties.minThreads(),
                properties.idleTimeout(),
                blockingQueueFactory.create(properties.queue()));
        threadPool.setName(properties.name());
        return threadPool;
    }
}
