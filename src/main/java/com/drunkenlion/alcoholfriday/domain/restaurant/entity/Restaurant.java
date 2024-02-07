package com.drunkenlion.alcoholfriday.domain.restaurant.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;
import org.springframework.data.geo.Point;

import java.util.HashMap;
import java.util.Map;

@Getter
@Entity
@SuperBuilder
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
    private Map<String, Object> menu = new HashMap<>();

    @Type(JsonType.class)
    @Comment("영업시간")
    @Column(name = "time", columnDefinition ="json")
    private Map<String, Object> time = new HashMap<>();

    @Type(JsonType.class)
    @Comment("레스토랑의 편의시설")
    @Column(name = "provision", columnDefinition ="json")
    private Map<String , Object> provision = new HashMap<>();
}
