package com.drunkenlion.alcoholfriday.domain.restaurant.dto.request;


import lombok.Builder;
import lombok.Getter;



@Getter
@Builder
public class LocationDataRequest {

    private Double latitude;

    private Double longitude;

    public LocationDataRequest(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}