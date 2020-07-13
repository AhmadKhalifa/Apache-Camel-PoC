package com.rtt.collector.collectorpoc.exception;

public class RTTCampaignNotFoundException extends ResourceNotFoundException {

    public RTTCampaignNotFoundException(long rttoolCampaignId) {
        super(String.format("RTTool campaign %d not found", rttoolCampaignId));
    }
}
