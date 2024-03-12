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
    @Comment("제조사 이름")
    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Comment("제조사 주소")
    @Column(name = "address", columnDefinition = "VARCHAR(200)")
    private String address;

    @Comment("제조사 상세 주소")
    @Column(name = "detail", columnDefinition = "VARCHAR(200)")
    private String detail;

    @Comment("술 제조지역")
    @Column(name = "region", columnDefinition = "VARCHAR(50)")
    private String region;
}
