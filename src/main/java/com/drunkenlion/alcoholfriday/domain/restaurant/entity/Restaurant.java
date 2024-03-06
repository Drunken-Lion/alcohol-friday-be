package com.drunkenlion.alcoholfriday.domain.restaurant.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Entity
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Restaurant extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member members;

    @Column(length = 50)
    @Comment("레스토랑 분류")
    private String category;

    @Column(length = 200)
    @Comment("레스토랑 이름")
    private String name;

    @Column(length = 200)
    @Comment("레스토랑 주소")
    private String address;

    @Comment("위도, 경도")
    private Point location;

    @Comment("가게 연락처")
    private Long contact;

    @Type(JsonType.class)
    @Comment("메뉴")
    @Column(name = "menu", columnDefinition ="json")
    @Builder.Default
    private Map<String, Object> menu = new HashMap<>();

    @Type(JsonType.class)
    @Comment("영업시간")
    @Column(name = "time", columnDefinition ="json")
    @Builder.Default
    private Map<String, Object> time = new HashMap<>();

    @Type(JsonType.class)
    @Comment("레스토랑의 편의시설")
    @Column(name = "provision", columnDefinition ="json")
    @Builder.Default
    private Map<String , Object> provision = new HashMap<>();

    @OneToMany(mappedBy = "restaurant")
    @Builder.Default
    private List<RestaurantStock> restaurantStocks = new ArrayList<>();

    public static Point genPoint(Double longitude, Double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory();
        //new Coordinate(경도, 위도)
        Coordinate coordinate = new Coordinate(longitude, latitude);
        //Point(위도 , 경도)
        return geometryFactory.createPoint(coordinate);
    }
}
