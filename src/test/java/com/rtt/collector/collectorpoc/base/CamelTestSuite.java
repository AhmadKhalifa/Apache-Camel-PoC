package com.rtt.collector.collectorpoc.base;

import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.model.ModelCamelContext;
import org.apache.camel.reifier.RouteReifier;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ActiveProfiles("local-testing")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public interface CamelTestSuite {

    RouteMockEndpoints[] getEndpointsToMock();

    boolean skipOriginalEndpoints();

    default void mockEndpoints(ModelCamelContext camelContext) throws Exception {
        RouteMockEndpoints[] endpointsToMock = getEndpointsToMock();
        if (!Arrays.isNullOrEmpty(endpointsToMock)) {
            for (RouteMockEndpoints routeMockEndpoints : endpointsToMock) {
                mock(camelContext, routeMockEndpoints.routeId, routeMockEndpoints.endpoints);
            }
        }
    }

    default void mock(ModelCamelContext camelContext, String routeId, String... endpoints) throws Exception {
        RouteReifier.adviceWith(
                camelContext.getRouteDefinition(routeId),
                camelContext,
                new AdviceWithRouteBuilder() {
                    @Override
                    public void configure() throws Exception {
                        if (skipOriginalEndpoints()) {
                            mockEndpointsAndSkip(endpoints);
                        } else {
                            mockEndpoints(endpoints);
                        }
                    }
                }
        );
    }

    class RouteMockEndpoints {

        private final String routeId;

        private final String[] endpoints;

        public RouteMockEndpoints(String routeId, String... endpoints) {
            this.routeId = routeId;
            this.endpoints = endpoints;
        }
    }
}
