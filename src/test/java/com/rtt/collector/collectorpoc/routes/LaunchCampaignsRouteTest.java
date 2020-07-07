//package com.rtt.collector.collectorpoc.routes;
//
//import com.rtt.collector.collectorpoc.bot.service.BotService;
//import com.rtt.collector.collectorpoc.bot.service.ComboBotService;
//import com.rtt.collector.collectorpoc.bot.usecase.ValidateBotUseCase;
//import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
//import org.apache.camel.builder.RouteBuilder;
//import org.apache.camel.component.mock.MockEndpoint;
//import org.apache.camel.test.junit5.CamelTestSupport;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.springframework.test.annotation.DirtiesContext;
//
//import static com.rtt.collector.collectorpoc.camel.utils.Constants.KEY_RTTOOL_CAMPAIGN_ID;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//public class LaunchCampaignsRouteTest extends CamelTestSupport {
//
//    @InjectMocks
//    private ValidateBotUseCase validateBotUseCase;
//
//    @Mock
//    private ComboBotService botService;
//
//    @Test
//    void testWhenBotIsValid() throws Exception {
//        when(botService.validateBot(any())).thenReturn(true);
//        MockEndpoint mock = getMockEndpoint("direct:chunkCampaign");
//        mock.expectedBodiesReceived(true);
//        template.requestBody("seda:launchCampaigns", new RTToolCampaign());
//        assertMockEndpointsSatisfied();
//    }
//
//    @Override
//    protected RouteBuilder createRouteBuilder() {
//        return new RouteBuilder() {
//            public void configure() {
//                from("seda:launchCampaigns")
//                        .process(exchange -> {
//                            RTToolCampaign rtToolCampaign = exchange.getIn().getBody(RTToolCampaign.class);
//                            long campaignId = rtToolCampaign.getId();
//                            exchange.getIn().setHeader(KEY_RTTOOL_CAMPAIGN_ID, campaignId);
//                            exchange.getIn().setBody(validateBotUseCase.execute(
//                                    ValidateBotUseCase.Parameters.build(rtToolCampaign.getBot())
//                            ));
//                        })
//                        .choice()
//                        .when(body().isEqualTo(true))
//                        .to("direct:chunkCampaign")
//                        .otherwise()
//                        .to("direct:markCampaignAsBotError")
//                        .end();
//            }
//        };
//    }
//}
