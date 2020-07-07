package com.rtt.collector.collectorpoc.bot.model;

import com.rtt.collector.collectorpoc.base.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "bot")
@Entity
public class BotEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;

    @Column(name = "bot_id")
    private String botHubId;

    @Column(name = "is_activated")
    private boolean isActivated;

    private boolean error;
}
