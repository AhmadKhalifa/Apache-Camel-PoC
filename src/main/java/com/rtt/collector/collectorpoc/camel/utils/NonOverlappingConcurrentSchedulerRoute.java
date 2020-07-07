package com.rtt.collector.collectorpoc.camel.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.SplitDefinition;

@Slf4j
public abstract class NonOverlappingConcurrentSchedulerRoute extends RouteBuilder
        implements ConcurrentTasksMonitor.Callback {

    private static final int DEFAULT_THREAD_POOL_SIZE = 10;

    protected final ConcurrentTasksMonitor tasksMonitor = new ConcurrentTasksMonitor(this);

    public int getThreadPool() {
        return DEFAULT_THREAD_POOL_SIZE;
    }

    public abstract String getRouteId();

    public abstract String getCronExpression();

    public abstract void initExceptionHandler();

    public abstract SplitDefinition buildSplitDefinition(RouteDefinition route);

    public abstract void buildTaskRoute(RouteDefinition route);

    @Override
    public void configure() {
        onException(Exception.class)
                .maximumRedeliveries(5)
                .redeliveryDelay(2000)
                .retryAttemptedLogLevel(LoggingLevel.WARN)
                .handled(true)
                .bean(tasksMonitor, "notifyTaskEnded");

        initExceptionHandler();

        from(String.format("quartz://%s?cron=%s", getRouteId(), getCronExpression()))
                .log(String.format("Route %s: starting new cycle", getRouteId()))
                .bean(tasksMonitor, "isProcessing")
                .choice()
                .when(body().isEqualTo(true))
                .log(String.format("Route %s: Previous cycle is still processing, ignoring this cycle", getRouteId()))
                .otherwise()
                .to(String.format("direct:%sProcess", getRouteId()));

        buildSplitDefinition(from(String.format("direct:%sProcess", getRouteId())))
                .process(exchange -> tasksMonitor.notifyTasksStarted((int) exchange.getProperty(Exchange.SPLIT_SIZE)))
                .to(String.format("seda:%sSEDA", getRouteId()));

        buildTaskRoute(from(String.format("seda:%sSEDA?concurrentConsumers=%d", getRouteId(), getThreadPool())));
    }



    @Override
    public void onStart(int totalTasks) {
        log.info("Route {}: Started {} concurrent tasks", getRouteId(), totalTasks);
    }

    @Override
    public void onProgress(int finishedTasks, int totalTasks) {
        log.info("Route {}: Finished {}/{} tasks", getRouteId(), finishedTasks, totalTasks);
    }

    @Override
    public void onFinish(int totalTasks) {
        log.info("Route {}: Finished all {} tasks", getRouteId(), totalTasks);
    }
}
