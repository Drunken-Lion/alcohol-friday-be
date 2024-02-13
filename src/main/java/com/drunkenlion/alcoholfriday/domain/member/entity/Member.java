package com.drunkenlion.alcoholfriday.domain.member.entity;

import com.drunkenlion.alcoholfriday.domain.auth.util.ProviderTypeConverter;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.member.util.MemberRoleConverter;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.Comment;

import java.time.LocalDate;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {
	@Comment("회원 메일")
	@Column(unique = true, length = 50)
	private String email;

	@Comment("회원 가입 소셜 정보")
	@Column(length = 20)
	@Convert(converter = ProviderTypeConverter.class)
	private ProviderType provider;

	@Comment("회원 본명")
	@Column(length = 50)
	private String name;

	@Comment("회원 별명")
	@Column(length = 50)
	private String nickname;

	@Comment("회원 권한")
	@Column(length = 50)
	@Convert(converter = MemberRoleConverter.class)
	private MemberRole role;

	@Comment("회원 연락처")
	private Long phone;

	@Comment("성인인증 날짜")
	private LocalDate certifyAt;

	@Comment("이용 약관 동의")
	private Boolean agreedToServiceUse;

	@Comment("개인정보 수집 이용 안내 동의")
	private Boolean agreedToServicePolicy;

	@Comment("개인정보 활용 동의")
	private Boolean agreedToServicePolicyUse;
}
