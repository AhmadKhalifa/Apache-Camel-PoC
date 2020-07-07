package com.rtt.collector.collectorpoc.campaign.combo.data;

import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaignEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BotHubCampaignRepository extends CrudRepository<BotHubCampaignEntity, Long> {

}
