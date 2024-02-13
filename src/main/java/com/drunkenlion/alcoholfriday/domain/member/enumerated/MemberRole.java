package com.drunkenlion.alcoholfriday.domain.member.enumerated;

import java.util.Arrays;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

import lombok.Getter;

@Getter
public enum MemberRole {
	/**
	 * 일반 회원
	 */
	MEMBER("MEMBER"),

	/**
	 *  관리자
	 */
	ADMIN("ADMIN"),

	/**
	 * 슈퍼바이저 (인사관리)
	 */
	SUPER_VISOR("SUPER_VISOR"),

	/**
	 * 스토어 관리자
	 */
	STORE_MANAGER("STORE_MANAGER"),

	/**
	 * 레스토랑 사장
	 */
	OWNER("OWNER");

	private final String role;

	MemberRole(String role) {
		this.role = role;
	}

	public static MemberRole ofRole(String role) {
		return Arrays.stream(MemberRole.values())
			.filter(value -> value.role.equals(role))
			.findFirst()
			.orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_ROLE));
	}
}
