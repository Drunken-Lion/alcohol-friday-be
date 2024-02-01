package com.drunkenlion.alcoholfriday.domain.restaurant.application;


import com.drunkenlion.alcoholfriday.domain.restaurant.dto.request.LocationDataRequest;
import com.drunkenlion.alcoholfriday.global.common.enumerated.Direction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SpatialFunctionServiceTest {
    Logger log = LoggerFactory.getLogger(SpatialFunctionService.class);

    @Autowired
    private SpatialFunctionService spatialFunctionService;


    @Test
    @DisplayName("사용자 의 정보의 위치 특정 도형을 감싸는 도형 테스트")
    public void mbr() {

        //give
        //사용자 위도
        double latitude = 35.8393357;
        //사용자 경도
        double longitude = 128.7210818;
        // 반지름
        double radius = 10.0;

        //when
        LocationDataRequest northEast = spatialFunctionService.calculateDestination(latitude, longitude, radius, Direction.NORTHEAST.getBearing());
        LocationDataRequest southWest = spatialFunctionService.calculateDestination(latitude, longitude, radius, Direction.SOUTHWEST.getBearing());


        //북동쪽 위도
        double x1 = northEast.getLatitude();
        //남동쪽 경도
        double y1 = northEast.getLongitude();
        //남서쪽 위도
        double x2 = southWest.getLatitude();
        //남서쪽 경도
        double y2 = southWest.getLongitude();

        String pointFormat = String.format("'LINESTRING(%f %f, %f %f)')", x1, y1, x2, y2);

        System.out.println(x1);
        System.out.println(y1);
        System.out.println(y2);
        System.out.println(x2);

    }

}
