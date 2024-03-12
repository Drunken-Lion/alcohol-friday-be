package com.drunkenlion.alcoholfriday.global.security.jwt.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.time.Instant;

@Getter
@Entity
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "refresh_token")
public class RefreshToken extends BaseEntity {
    @Column(name = "token", columnDefinition = "VARCHAR(512)")
    @Comment("리프레시 토큰")
    private String token;

    @Column(name = "expiry_date", columnDefinition = "DATETIME")
    @Comment("리프레시 토큰 만료 일자")
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @Comment("해당 토큰을 가진 회원 정보")
    private Member member;
}
