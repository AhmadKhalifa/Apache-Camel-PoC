package com.rtt.collector.collectorpoc.base;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseCamelRouteUnitTestSuite<T extends BaseRoute>
        extends CamelTestSupport implements CamelTestSuite {

    protected abstract T getRoute() throws Exception;

    @Override
    public boolean skipOriginalEndpoints() {
        return true;
    }

    @BeforeEach
    protected void mockAndSkipEndpoints() throws Exception {
        mockEndpoints(context);
    }

    @Override
    final protected RoutesBuilder createRouteBuilder() throws Exception {
        return getRoute();
    }
}
