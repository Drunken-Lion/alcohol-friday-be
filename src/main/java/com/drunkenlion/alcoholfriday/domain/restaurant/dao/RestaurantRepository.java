package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {

    @Query(value = "SELECT * FROM restaurant r WHERE ST_Contains(" +
            "Polygon(LineString(" +
            "Point(:swLongitude, :neLatitude), " + // 북서(NW) 꼭짓점
            "Point(:neLongitude, :neLatitude), " + // 북동(NE) 꼭짓점
            "Point(:neLongitude, :swLatitude), " + // 남동(SE) 꼭짓점
            "Point(:swLongitude, :swLatitude), " + // 남서(SW) 꼭짓점
            "Point(:swLongitude, :neLatitude))), r.location)", // 폴리곤을 닫기 위해 북서(NW) 꼭짓점 반복
            nativeQuery = true)
    List<Restaurant> findAllWithinPolygon(
            @Param("neLatitude") double neLatitude,
            @Param("neLongitude") double neLongitude,
            @Param("swLatitude") double swLatitude,
            @Param("swLongitude") double swLongitude);


}
