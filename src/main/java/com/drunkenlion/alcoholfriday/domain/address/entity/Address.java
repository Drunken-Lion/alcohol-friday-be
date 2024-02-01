package com.drunkenlion.alcoholfriday.domain.address.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {
    @Comment("주소 소유자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Comment("대표 주소 여부")
    private Boolean isPrimary;

    @Comment("주소")
    @Column(length = 200)
    private String address;

    @Comment("상세 주소")
    @Column(length = 200)
    private String detail;

    @Comment("우편번호")
    private Long postcode;
}
