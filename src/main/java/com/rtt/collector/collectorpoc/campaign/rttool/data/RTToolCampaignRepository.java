package com.rtt.collector.collectorpoc.campaign.rttool.data;

import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaignEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RTToolCampaignRepository extends CrudRepository<RTToolCampaignEntity, Long> {

    List<RTToolCampaignEntity> findAllByStatus(int status);
}
