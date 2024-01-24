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
    private String category;

    @Column(length = 200)
    private String name;

    @Column(length = 200)
    private String address;

    private Point location;

    private Long contact;

    @Type(JsonType.class)
    @Column(name = "menu", columnDefinition ="json")
    private Map<String, Object> menu = new HashMap<>();

    @Type(JsonType.class)
    @Column(name = "time", columnDefinition ="json")
    private Map<String, Object> time = new HashMap<>();
}
