package com.rtt.collector.collectorpoc.campaign.combo.model;

import com.rtt.collector.collectorpoc.base.BaseDto;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaign;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@ToString
public class BotHubCampaign implements BaseDto {

    private long id;

    private RTToolCampaign rtToolCampaign;

    private String botHubId;

    private int sentCount;

    private int deliveredCount;

    private int readCount;

    private int errorCount;

    private int totalCount;
}
