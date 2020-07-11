package com.rtt.collector.collectorpoc.base;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.rtt.collector.collectorpoc.camel.schuedler.CollectorScheduler;
import com.rtt.collector.collectorpoc.camel.schuedler.TriggerScheduler;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.reifier.RouteReifier;
import org.apache.camel.spring.boot.SpringBootCamelContext;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@SpringBootTest
@ActiveProfiles("local-testing")
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestExecutionListeners(
        value = {
                TransactionalTestExecutionListener.class,
                DependencyInjectionTestExecutionListener.class,
                DbUnitTestExecutionListener.class
        },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public abstract class BaseCamelRouteIntegrationTestSuite {

    private static final String[] schedulersRouteIds = {TriggerScheduler.ROUTE_ID, CollectorScheduler.ROUTE_ID};

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    protected ProducerTemplate producerTemplate;

    protected abstract RouteMockEndpoints[] getEndpointsToMock();

    @BeforeEach
    final protected void initialize() throws Exception {
        stopSchedulers();
        mockEndpoints();
    }

    private void stopSchedulers() throws Exception {
        for (String schedulerRouteId : schedulersRouteIds) {
            camelContext.getRouteController().suspendRoute(schedulerRouteId);
        }
    }

    private void mockEndpoints() throws Exception {
        RouteMockEndpoints[] endpointsToMock = getEndpointsToMock();
        if (!Arrays.isNullOrEmpty(endpointsToMock)) {
            for (RouteMockEndpoints routeMockEndpoints : endpointsToMock) {
                mock(routeMockEndpoints.routeId, routeMockEndpoints.endpoints);
            }
        }
    }

    private void mock(String routeId, String... endpoints) throws Exception {
        if (camelContext instanceof SpringBootCamelContext) {
            RouteReifier.adviceWith(
                    ((SpringBootCamelContext) camelContext).getRouteDefinition(routeId),
                    camelContext,
                    new AdviceWithRouteBuilder() {

                        @Override
                        public void configure() throws Exception {
                            mockEndpoints(endpoints);
                        }
                    }
            );
        }
    }

    final protected void assertMockEndpointsSatisfied() throws Exception {
        MockEndpoint.assertIsSatisfied(camelContext);
    }

    public static class RouteMockEndpoints {

        private final String routeId;

        private final String[] endpoints;

        public RouteMockEndpoints(String routeId, String... endpoints) {
            this.routeId = routeId;
            this.endpoints = endpoints;
        }
    }
}
