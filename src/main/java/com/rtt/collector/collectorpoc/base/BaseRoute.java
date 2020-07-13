package com.rtt.collector.collectorpoc.base;

import com.rtt.collector.collectorpoc.annotation.Route;
import com.rtt.collector.collectorpoc.camel.predicate.ExceptionTypePredicate;
import com.rtt.collector.collectorpoc.exception.BotHubCampaignNotFoundException;
import com.rtt.collector.collectorpoc.exception.BotNotFoundException;
import com.rtt.collector.collectorpoc.exception.RTTCampaignNotFoundException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;

import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_CAUGHT_EXCEPTION;

@Route
public abstract class BaseRoute extends RouteBuilder {

    public static final String ROUTE_ID = BaseRoute.class.getSimpleName();

    @Value("${camel.variables.maximum-redeliveries}")
    int maximumRedeliveries;

    @Value("${camel.variables.maximum-redelivery-delay}")
    int maximumRedeliveryDelay;

    @Override
    public void configure() throws Exception {

        errorHandler(deadLetterChannel("direct:deadLetterChannel")
                .maximumRedeliveries(maximumRedeliveries)
                .maximumRedeliveryDelay(maximumRedeliveryDelay)
                .onPrepareFailure(exchange -> {
                    Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
                    exchange.getIn().setHeader(KEY_CAUGHT_EXCEPTION, exception);
                })
        );

        from("direct:deadLetterChannel")
                .routeId(ROUTE_ID)
                .choice()
                    .when(ExceptionTypePredicate.is(RTTCampaignNotFoundException.class))
                        .to("direct:rttCampaignNotFoundHandler")
                    .when(ExceptionTypePredicate.is(BotHubCampaignNotFoundException.class))
                        .to("direct:botHubCampaignNotFoundHandler")
                    .when(ExceptionTypePredicate.is(BotNotFoundException.class))
                        .to("direct:botNotFoundHandler")
                    .otherwise()
                        .to("direct:generalExceptionHandler")
                .endChoice();
    }
}
