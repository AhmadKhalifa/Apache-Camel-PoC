package com.rtt.collector.collectorpoc.util;

import com.rtt.collector.collectorpoc.annotation.Util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Util
public class MsisdnGenerator {

    public List<String> generate(int msisdnsCount) {
        HashSet<String> hashSet = new HashSet<>();
        StringBuilder stringBuilder;
        boolean use010 = true;
        Random random = new Random();
        while (hashSet.size() < msisdnsCount){
            stringBuilder = new StringBuilder("201");
            stringBuilder.append(use010 ? "0" : "2");
            for (int j = 0; j < 7; j++) {
                stringBuilder.append((char) ('0' + random.nextInt(10)));
            }
            hashSet.add(stringBuilder.toString());
            use010 = !use010;
        }
        return new ArrayList<>(hashSet);
    }
}
