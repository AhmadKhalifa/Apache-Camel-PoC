package com.rtt.collector.collectorpoc.campaign.rttool.model;

import com.rtt.collector.collectorpoc.base.BaseDto;
import com.rtt.collector.collectorpoc.bot.model.Bot;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaign;
import com.rtt.collector.collectorpoc.campaign.msisdn.Msisdn;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class RTToolCampaign implements BaseDto {

    private long id;

    private Bot bot;

    private String name;

    private Status status;

    private List<BotHubCampaign> botHubCampaigns;

    private List<Msisdn> msisdns;

    public enum Status {NOT_STARTED, ACTIVE, STOPPED, BOT_ERROR, FINISHED}
}
