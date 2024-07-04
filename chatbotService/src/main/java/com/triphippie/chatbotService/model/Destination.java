package com.triphippie.chatbotService.model;

import jdk.jfr.Description;
import lombok.Data;

import java.util.List;

@Data
public class Destination {
    @Description("Must be less than 20 words")
    private String history;

    @Description("Must be less than 20 words")
    private String culture;

    private List<String> typicalDishes;

    @Description("Must be less than 20 words")
    private String bestPeriodToVisit;

    private List<String> museums;

    private List<String> monuments;
}
