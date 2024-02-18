package com.drunkenlion.alcoholfriday.global.common.enumerated;

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

    private String value;


    MemberRole(String member) {
    }

    public String getRole() {
        return this.value;
    }
}
