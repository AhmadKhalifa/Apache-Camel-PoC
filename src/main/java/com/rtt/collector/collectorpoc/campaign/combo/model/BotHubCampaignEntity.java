package com.rtt.collector.collectorpoc.campaign.combo.model;

import com.rtt.collector.collectorpoc.base.BaseEntity;
import com.rtt.collector.collectorpoc.campaign.rttool.model.RTToolCampaignEntity;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "bot_hub_campaign")
@Entity
@Builder
public class BotHubCampaignEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "campaign_id")
    private RTToolCampaignEntity rtToolCampaign;

    @Column(name = "bot_hub_id")
    private String botHubId;

    @Column(name = "sent_count")
    private int sentCount;

    @Column(name = "delivered_count")
    private int deliveredCount;

    @Column(name = "read_count")
    private int readCount;

    @Column(name = "error_count")
    private int errorCount;

    @Column(name = "total_count")
    private int totalCount;
}