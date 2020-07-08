package com.rtt.collector.collectorpoc.routes;

import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class CollectRTToolCampaignsRouteTest extends CamelTestSupport {

}