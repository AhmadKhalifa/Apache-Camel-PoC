package com.rtt.collector.collectorpoc.campaign.msisdn;

import com.rtt.collector.collectorpoc.base.BaseEntity;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaignEntity;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "msisdn")
@Entity
@Builder
public class MsisdnEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "campaign_id")
    private RTToolCampaignEntity rtToolCampaign;

    private String number;
}
