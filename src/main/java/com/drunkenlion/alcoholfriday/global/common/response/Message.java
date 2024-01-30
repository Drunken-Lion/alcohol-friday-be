package com.drunkenlion.alcoholfriday.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Message {
    @Getter
    @RequiredArgsConstructor
    public enum Success {
        // 200
        OK("성공"),
        CREATED("리소스가 생성되었습니다."),
        NO_CONTENT("성공하였지만, 리소스가 없습니다."),

        // 300
        MOVED_PERMANENTLY("영구적으로 이동합니다."),
        FOUND("다른 URI에서 리소스를 찾았습니다."),
        NOT_MODIFIED("캐시를 사용하세요."),
        TEMPORARY_REDIRECT("다른 URI에서 리소스를 찾았고 본문도 유지됩니다."),
        PERMANENT_REDIRECT("영구적으로 이동하고 본문도 유지됩니다.");

        private final String message;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Fail {
        // 400
        BAD_REQUEST("잘못된 요청 입니다."),
        INVALID_INPUT_VALUE("잘못된 입력값 입니다."),

        // 401
        UNAUTHORIZED("인증되지 않은 접근입니다."),
        INVALID_TOKEN("유효하지 않은 JWT 토큰입니다."),
        EXPIRED_TOKEN("만료된 JWT 토큰입니다."),
        UNAUTHORIZED_TOKEN("지원하지 않는 JWT 토큰입니다."),
        WRONG_TOKEN("잘못된 JWT 토큰입니다."),
        INVALID_ACCOUNT("계정정보가 일치하지 않습니다."),

        // 403
        FORBIDDEN("권한이 없는 접근입니다."),
        DEACTIVATE_USER("비활성화 상태 계정입니다."),

        // 404
        NOT_FOUND_OBJECT("존재하지 않는 리소스입니다."),

        // 405
        METHOD_NOT_ALLOWED("허용되지 않은 HTTP METHOD 입니다."),

        // 409
        CONFLICT("이미 리소스가 존재합니다."),

        // 500 서버 에러
        INTERNAL_SERVER_ERROR("서버 에러 입니다.");

        private final String message;
    }
}
