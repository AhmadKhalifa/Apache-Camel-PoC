package com.rtt.collector.collectorpoc.campaign.msisdn;

import com.rtt.collector.collectorpoc.base.BaseDto;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class Msisdn implements BaseDto {

    private long id;

    private RTToolCampaign rtToolCampaign;

    private String number;
}
