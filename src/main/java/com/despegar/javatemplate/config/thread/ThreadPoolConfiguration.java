package com.despegar.javatemplate.config.thread;

import com.despegar.aftersales.threadpoolinsightsmetrics.PoolMetricsNotifier;
import com.despegar.javatemplate.config.server.factory.JettyThreadPoolFactory;
import com.despegar.javatemplate.config.server.model.JettyThreadPoolProperties;
import com.despegar.javatemplate.config.thread.factory.ThreadPoolExecutorFactory;
import com.despegar.javatemplate.config.thread.model.ExecutorsProperties;
import com.despegar.javatemplate.config.thread.model.ThreadPoolNotifierProperties;
import com.despegar.javatemplate.util.Beans;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

import static java.util.Optional.ofNullable;

@Configuration
public class ThreadPoolConfiguration {

    @Bean("jettyThreadPool")
    public ThreadPool jettyThreadPool(JettyThreadPoolProperties jettyThreadPoolProperties,
                                      JettyThreadPoolFactory serverThreadPoolFactory,
                                      PoolMetricsNotifier poolMetricsNotifier) {
        ThreadPool jettyThreadPool = serverThreadPoolFactory.create(jettyThreadPoolProperties);

        if (jettyThreadPool instanceof ExecutorThreadPool pool)
            poolMetricsNotifier.addPool("jettyThreadPool", pool);

        if (jettyThreadPool instanceof QueuedThreadPool pool)
            poolMetricsNotifier.addPool("jettyThreadPool", pool);

        return jettyThreadPool;
    }

    @Bean
    public PoolMetricsNotifier poolMetricsNotifier(ThreadPoolNotifierProperties poolNotifierProperties) {
        var poolMetricsNotifier = new PoolMetricsNotifier();

        ofNullable(poolNotifierProperties.scheduleInSeconds())
                .ifPresent(poolMetricsNotifier::schedule);

        return poolMetricsNotifier;
    }

    @Autowired
    public void registerExecutors(GenericWebApplicationContext applicationContext,
                                  ExecutorsProperties threadPoolExecutorsProperties,
                                  ThreadPoolExecutorFactory threadPoolExecutorFactory,
                                  PoolMetricsNotifier poolMetricsNotifier) {
        ofNullable(threadPoolExecutorsProperties.executors()).orElse(Map.of()).values()
                .forEach(threadPoolExecutorProperties -> {
                    var name = Beans.toBeanName(threadPoolExecutorProperties.name());
                    var executor = threadPoolExecutorFactory.create(threadPoolExecutorProperties);
                    poolMetricsNotifier.addPool(name, executor);
                    applicationContext.registerBean(
                            name,
                            ThreadPoolExecutor.class,
                            () -> executor,
                            bd -> bd.setAutowireCandidate(true)
                    );
                });
    }
}
