package com.drunkenlion.alcoholfriday.domain.store.entity;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    private Boolean isPrimary;

    @Column(length = 200)
    private String address;

    @Column(length = 200)
    private String detail;

    private Long postcode;
}
