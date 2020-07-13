package com.rtt.collector.collectorpoc.base;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.rtt.collector.collectorpoc.camel.schuedler.CollectorScheduler;
import com.rtt.collector.collectorpoc.camel.schuedler.TriggerScheduler;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.model.ModelCamelContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

@SpringBootTest
@TestExecutionListeners(
        value = {
                TransactionalTestExecutionListener.class,
                DependencyInjectionTestExecutionListener.class,
                DbUnitTestExecutionListener.class
        },
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
public abstract class BaseCamelRouteIntegrationTestSuite implements CamelTestSuite {

    private static final String[] schedulersRouteIds = {TriggerScheduler.ROUTE_ID, CollectorScheduler.ROUTE_ID};

    @Autowired
    protected CamelContext camelContext;

    @Autowired
    protected ProducerTemplate producerTemplate;

    @Override
    public boolean skipOriginalEndpoints() {
        return false;
    }

    @BeforeEach
    final protected void initialize() throws Exception {
        stopSchedulers();
        if (camelContext instanceof ModelCamelContext) {
            mockEndpoints((ModelCamelContext) camelContext);
        }
    }

    private void stopSchedulers() throws Exception {
        for (String schedulerRouteId : schedulersRouteIds) {
            camelContext.getRouteController().suspendRoute(schedulerRouteId);
        }
    }

    final protected void assertMockEndpointsSatisfied() throws Exception {
        MockEndpoint.assertIsSatisfied(camelContext);
    }
}
