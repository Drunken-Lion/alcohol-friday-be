package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity;

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
    private Member member;

    @Comment("레스토랑 분류")
    @Column(name = "category", columnDefinition = "VARCHAR(50)")
    private String category;

    @Comment("레스토랑 이름")
    @Column(name = "name", columnDefinition = "VARCHAR(200)")
    private String name;

    @Comment("레스토랑 주소")
    @Column(name = "address", columnDefinition = "VARCHAR(200)")
    private String address;

    @Comment("레스토랑 상세 주소")
    @Column(name = "address_detail", columnDefinition = "VARCHAR(200)")
    private String addressDetail;

    @Comment("우편번호")
    @Column(name = "postcode", columnDefinition = "VARCHAR(50)")
    private String postcode;

    @Comment("위도, 경도")
    @Column(name = "location", columnDefinition = "POINT")
    private Point location;

    @Comment("가게 연락처")
    @Column(name = "contact", columnDefinition = "BIGINT")
    private Long contact;

    @Comment("사업자 등록증 내 기재된 사업장명")
    @Column(name = "businessName", columnDefinition = "VARCHAR(50)")
    private String businessName;

    @Comment("사업자 등록증 내 기재된 사업자번호")
    @Column(name = "businessNumber", columnDefinition = "VARCHAR(50)")
    private String businessNumber;

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
