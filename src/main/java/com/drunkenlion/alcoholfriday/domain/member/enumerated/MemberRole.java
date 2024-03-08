package com.drunkenlion.alcoholfriday.domain.member.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MemberRole {
    /**
     * 일반 회원
     */
    MEMBER("MEMBER", "5"),

    /**
     * 관리자
     */
    ADMIN("ADMIN", "1"),

    /**
     * 슈퍼바이저 (인사관리)
     */
    SUPER_VISOR("SUPER_VISOR", "2"),

    /**
     * 스토어 관리자
     */
    STORE_MANAGER("STORE_MANAGER", "3"),

    /**
     * 레스토랑 사장
     */
    OWNER("OWNER", "4");

    private final String role;
    private final String roleNumber;

    MemberRole(String role, String roleNumber) {
        this.role = role;
        this.roleNumber = roleNumber;
    }

    public static MemberRole byRole(String role) {
        return Arrays.stream(MemberRole.values())
                .filter(value -> value.getRole().equals(role))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ROLE)
                        .build());
    }

    public static MemberRole byRoleNumber(String roleNumber) {
        return Arrays.stream(MemberRole.values())
                .filter(value -> value.getRoleNumber().equals(roleNumber))
                .findAny()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ROLE_NUMBER)
                        .build());
    }
}
