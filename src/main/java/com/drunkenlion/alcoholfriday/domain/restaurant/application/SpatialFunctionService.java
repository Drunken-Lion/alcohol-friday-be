package com.drunkenlion.alcoholfriday.domain.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.restaurant.dto.request.LocationDataRequest;

public interface SpatialFunctionService {

    LocationDataRequest calculateDestination(double startLatitude, double startLongitude, double travelDistance, double travelBearing);
}
