package com.drunkenlion.alcoholfriday.domain.restaurant.application;


import com.drunkenlion.alcoholfriday.domain.restaurant.dto.request.LocationDataRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class SpatialFunctionServiceImpl implements SpatialFunctionService{

    @Override
    public LocationDataRequest calculateDestination(double startLatitude, double startLongitude, double travelDistance, double travelBearing) {

        //반 지름 설정
        double travelBearingRadians = Math.toRadians(travelBearing);
        //위도
        double startLatitudeRadians = Math.toRadians(startLatitude);
        //경도
        double startLongitudeRadians = Math.toRadians(startLongitude);
        // 지구 반지름
        double angularDistance = travelDistance / 6378.1370;

        // 구면 삼각법 공식을 사용하여 목적지 위도를 계산
        double destinationLatitudeRadians = Math.asin(
                Math.sin(startLatitudeRadians) * Math.cos(angularDistance) +
                        Math.cos(startLatitudeRadians) * Math.sin(angularDistance) *
                                Math.cos(travelBearingRadians)
        );

        // 구면 삼각법 공식을 사용하여 목적지 경도를 계산
        double destinationLongitudeRadians = startLongitudeRadians +
                Math.atan2(Math.sin(travelBearingRadians) * Math.sin(angularDistance) * Math.cos(startLatitudeRadians),
                        Math.cos(angularDistance) - Math.sin(startLatitudeRadians) * Math.sin(destinationLatitudeRadians)
                );

        //각도 = 라디안 * 180 / π
        return new LocationDataRequest(Math.toDegrees(destinationLatitudeRadians), Math.toDegrees(destinationLongitudeRadians));


    }
}
