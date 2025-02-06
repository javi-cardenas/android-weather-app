package edu.uiuc.cs427app.data.models;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class WeatherInformation {

    private String weatherCondition;
    private String temperature;
    private String humidity;
    private String windCondition;

}
