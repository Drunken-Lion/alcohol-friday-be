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
@Table(name = "restaurant")
public class Restaurant extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member members;

    @Comment("레스토랑 분류")
    @Column(name = "category", columnDefinition = "VARCHAR(50)")
    private String category;

    @Comment("레스토랑 이름")
    @Column(name = "name", columnDefinition = "VARCHAR(200)")
    private String name;

    @Comment("레스토랑 주소")
    @Column(name = "address", columnDefinition = "VARCHAR(200)")
    private String address;

    @Comment("위도, 경도")
    @Column(name = "location", columnDefinition = "POINT")
    private Point location;

    @Comment("가게 연락처")
    @Column(name = "contact", columnDefinition = "BIGINT")
    private Long contact;

    @Type(JsonType.class)
    @Comment("메뉴")
    @Column(name = "menu", columnDefinition ="JSON")
    @Builder.Default
    private Map<String, Object> menu = new HashMap<>();

    @Type(JsonType.class)
    @Comment("영업시간")
    @Column(name = "time", columnDefinition ="JSON")
    @Builder.Default
    private Map<String, Object> time = new HashMap<>();

    @Type(JsonType.class)
    @Comment("레스토랑의 편의시설")
    @Column(name = "provision", columnDefinition ="JSON")
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
