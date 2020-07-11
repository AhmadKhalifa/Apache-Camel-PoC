package com.rtt.collector.collectorpoc.base;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.reifier.RouteReifier;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public abstract class BaseCamelRouteUnitTestSuite<T extends BaseRoute> extends CamelTestSupport {

    protected abstract T getRoute() throws Exception;

    protected abstract String[] getEndpointsToMock();

    @BeforeEach
    protected void mockAndSkipEndpoints() throws Exception {
        String[] endpointsToMock = getEndpointsToMock();
        if (!Arrays.isNullOrEmpty(endpointsToMock)) {
            RouteReifier.adviceWith(context.getRouteDefinitions().get(0), context, new AdviceWithRouteBuilder() {
                @Override
                public void configure() throws Exception {
                    mockEndpointsAndSkip(endpointsToMock);
                }
            });
        }
    }

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return getRoute();
    }
}
