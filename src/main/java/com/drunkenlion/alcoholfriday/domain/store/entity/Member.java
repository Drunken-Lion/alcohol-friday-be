package com.drunkenlion.alcoholfriday.domain.store.entity;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Column(unique = true, length = 50)
    private String email;

    @Column(length = 20)
    private String provider;

    @Column(length = 50)
    private String name;

    @Column(length = 50)
    private String nickname;

    @Column(length = 50)
    private String role;

    private Long phone;

    private LocalDate birthday;

    private Boolean agreedToServiceUse;

    private Boolean agreedToServicePolicy;

    private Boolean agreedToServicePolicyUse;
}
