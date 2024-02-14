package com.drunkenlion.alcoholfriday.domain.maker.entity;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "maker")
public class Maker extends BaseEntity {
    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    @Comment("제조사 이름")
    private String name;

    @Column(name = "address", columnDefinition = "VARCHAR(200)")
    @Comment("제조사 주소")
    private String address;

    @Column(name = "detail", columnDefinition = "VARCHAR(200)")
    @Comment("제조사 상세 주소")
    private String detail;

    @Column(name = "region", columnDefinition = "VARCHAR(50)")
    @Comment("술 제조지역")
    private String region;
}
