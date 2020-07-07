package com.rtt.collector.collectorpoc.bot.model;

import com.rtt.collector.collectorpoc.base.BaseDto;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class Bot implements BaseDto {

    private long id;

    private String name;

    private String botHubId;

    private boolean isActivated;

    private boolean error;
}
