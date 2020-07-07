package com.rtt.collector.collectorpoc.campaign.rttool.model;

import com.rtt.collector.collectorpoc.base.BaseEntity;
import com.rtt.collector.collectorpoc.bot.model.BotEntity;
import com.rtt.collector.collectorpoc.campaign.combo.model.BotHubCampaignEntity;
import com.rtt.collector.collectorpoc.campaign.msisdn.MsisdnEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "campaign")
@Entity
public class RTToolCampaignEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "bot_id")
    private BotEntity bot;

    private String name;

    private int status;

    @OneToMany(
            mappedBy = "rtToolCampaign",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<BotHubCampaignEntity> botHubCampaigns;

    @OneToMany(
            mappedBy = "rtToolCampaign",
            fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH}
    )
    private Set<MsisdnEntity> msisdns;
}