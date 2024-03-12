package com.drunkenlion.alcoholfriday.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

public class HttpResponse {
    @Getter
    @RequiredArgsConstructor
    public enum Success {
        // 200
        OK(HttpStatus.OK, "성공"),
        CREATED(HttpStatus.CREATED, "리소스가 생성되었습니다."),
        NO_CONTENT(HttpStatus.NO_CONTENT, "성공하였지만, 리소스가 없습니다."),

        // 300
        MOVED_PERMANENTLY(HttpStatus.MOVED_PERMANENTLY, "영구적으로 이동합니다."),
        FOUND(HttpStatus.FOUND, "다른 URI에서 리소스를 찾았습니다."),
        NOT_MODIFIED(HttpStatus.NOT_MODIFIED, "캐시를 사용하세요."),
        TEMPORARY_REDIRECT(HttpStatus.TEMPORARY_REDIRECT, "다른 URI에서 리소스를 찾았고 본문도 유지됩니다."),
        PERMANENT_REDIRECT(HttpStatus.PERMANENT_REDIRECT, "영구적으로 이동하고 본문도 유지됩니다.");

        private final HttpStatus status;
        private final String message;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Fail {
        // 400
        BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청 입니다."),
        INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값 입니다."),
        ADDRESS_LIMIT_OVER(HttpStatus.BAD_REQUEST, "주소는 3개까지 등록할 수 있습니다."),

        // 401
        UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 접근입니다."),
        INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다."),
        EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 JWT 토큰입니다."),
        UNAUTHORIZED_TOKEN(HttpStatus.UNAUTHORIZED, "지원하지 않는 JWT 토큰입니다."),
        WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 JWT 토큰입니다."),
        INVALID_ACCOUNT(HttpStatus.UNAUTHORIZED, "계정정보가 일치하지 않습니다."),

        // 403
        FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없는 접근입니다."),
        DEACTIVATE_USER(HttpStatus.FORBIDDEN, "비활성화 상태 계정입니다."),

        // 404
        NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리소스입니다."),
        NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다."),
        NOT_FOUND_ROLE(HttpStatus.NOT_FOUND, "존재하지 않는 권한입니다."),
        NOT_FOUND_ROLE_NUMBER(HttpStatus.NOT_FOUND, "존재하지 않는 Role Number"),
        NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
        NOT_FOUND_RESTAURANT(HttpStatus.NOT_FOUND, "존재하지 않는 매장입니다."),
        NOT_FOUND_MAKER(HttpStatus.NOT_FOUND, "존재하지 않는 제조사입니다."),
        NOT_FOUND_ITEM(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
        NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "존재하지 않는 제품입니다."),
        NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 소분류입니다."),
        NOT_FOUND_CATEGORY_CLASS(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 대분류입니다."),
        NOT_FOUND_CART(HttpStatus.NOT_FOUND, "장바구니에 상품 내역이 없습니다."),
        NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "지원하지 않는 로그인입니다."),
        NOT_FOUND_PROVIDER_NUMBER(HttpStatus.NOT_FOUND, "존재하지 않는 Provider Number"),
        NOT_FOUND_ADDRESSES(HttpStatus.NOT_FOUND, "등록된 주소가 없습니다."),
        NOT_FOUND_ADDRESS(HttpStatus.NOT_FOUND, "존재하지 않는 주소f입니다."),
        NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND, "존재하지 않는 공지사항입니다."),
        NOT_FOUND_QUESTION(HttpStatus.NOT_FOUND, "존재하지 않는 문의입니다."),
        NOT_FOUND_RESTAURANT_ORDER_NUMBER(HttpStatus.NOT_FOUND, "존재하지 않는 Restaurant Order Number"),

        // 405
        METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP METHOD 입니다."),

        // 409
        CONFLICT(HttpStatus.CONFLICT, "이미 리소스가 존재합니다."),
        MAKER_IN_USE(HttpStatus.CONFLICT, "사용중인 제조사입니다."),
        NICKNAME_IN_USE(HttpStatus.CONFLICT, "사용중인 닉네임입니다."),
        PRODUCT_IN_USE(HttpStatus.CONFLICT, "사용중인 제품입니다."),
        CATEGORY_IN_USE(HttpStatus.CONFLICT, "사용중인 카테고리입니다."),

        // 500 서버 에러
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러 입니다.");

        private final HttpStatus status;
        private final String message;
    }
}
