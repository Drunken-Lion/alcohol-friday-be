package com.drunkenlion.alcoholfriday.domain.member.entity;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.auth.util.ProviderTypeConverter;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.member.util.MemberRoleConverter;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {
    @Comment("회원 메일")
    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(50)")
    private String email;

    @Comment("회원 가입 소셜 정보")
    @Column(name = "provider", columnDefinition = "VARCHAR(20)")
    @Convert(converter = ProviderTypeConverter.class)
    private ProviderType provider;

    @Comment("회원 본명")
    @Column(name = "name", columnDefinition = "VARCHAR(50)")
    private String name;

    @Comment("회원 별명")
    @Column(name = "nickname", columnDefinition = "VARCHAR(50)")
    private String nickname;

    @Comment("회원 권한")
    @Column(name = "role", columnDefinition = "VARCHAR(50)")
    @Convert(converter = MemberRoleConverter.class)
    private MemberRole role;

    @Comment("회원 연락처")
    @Column(name = "phone", columnDefinition = "BIGINT")
    private Long phone;

    @Comment("성인인증 날짜")
    @JoinColumn(name = "certify_at", columnDefinition = "DATE")
    private LocalDate certifyAt;

    @Comment("이용 약관 동의")
    @Column(name = "agreed_to_service_use", columnDefinition = "TINYINT")
    private Boolean agreedToServiceUse;

    @Comment("개인정보 수집 이용 안내 동의")
    @Column(name = "agreed_to_service_policy", columnDefinition = "TINYINT")
    private Boolean agreedToServicePolicy;

    @Comment("개인정보 활용 동의")
    @Column(name = "agreed_to_service_policy_use", columnDefinition = "TINYINT")
    private Boolean agreedToServicePolicyUse;

    @Comment("회원의 문의 내역")
    @OneToMany(mappedBy = "member")
    @Builder.Default
    private List<Question> questions = new ArrayList<>();
}
