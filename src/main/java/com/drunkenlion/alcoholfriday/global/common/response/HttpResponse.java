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
        BAD_REQUEST_AMOUNT(HttpStatus.BAD_REQUEST, "잘못된 가격값 입니다."),
        OUT_OF_PRODUCT_STOCK(HttpStatus.BAD_REQUEST, "현재 제품에 재고보다 많은 양을 추가할 수 없습니다."),
        INVALID_INPUT_PRODUCT_QUANTITY(HttpStatus.BAD_REQUEST, "제품 수량은 0 이상이어야 입니다."),
        MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "필수 파라미터가 누락되었습니다."),
        STOCK_NOT_NEGATIVE(HttpStatus.BAD_REQUEST, "재고 수량은 0보다 적을 수 없습니다."),
        PRICE_NOT_NEGATIVE(HttpStatus.BAD_REQUEST, "가격은 0보다 적을 수 없습니다."),
        PRICE_AND_STOCK_NOT_NEGATIVE(HttpStatus.BAD_REQUEST, "가격과 수량은 0보다 적을 수 없습니다."),
        PAYMENT_CANCEL_FAIL(HttpStatus.BAD_REQUEST, "결제 취소에 실패하였습니다."),

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
        STOCK_ADDITION_FORBIDDEN(HttpStatus.FORBIDDEN, "재고에 대한 수량 추가 권한이 없습니다."),

        // 404
        NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 리소스입니다."),
        NOT_FOUND_FILE(HttpStatus.NOT_FOUND, "존재하지 않는 파일입니다."),
        NOT_FOUND_ROLE(HttpStatus.NOT_FOUND, "존재하지 않는 권한입니다."),
        NOT_FOUND_ROLE_NUMBER(HttpStatus.NOT_FOUND, "존재하지 않는 Role Number"),
        NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),
        NOT_FOUND_RESTAURANT(HttpStatus.NOT_FOUND, "존재하지 않는 매장입니다."),
        NOT_FOUND_RESTAURANT_REFUND(HttpStatus.NOT_FOUND, "존재하지 않는 매장 환불입니다."),
        NOT_FOUND_RESTAURANT_REFUND_DETAIL(HttpStatus.NOT_FOUND, "존재하지 않는 매장 환불 상세입니다."),
        NOT_FOUND_MAKER(HttpStatus.NOT_FOUND, "존재하지 않는 제조사입니다."),
        NOT_FOUND_ITEM(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
        NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "존재하지 않는 제품입니다."),
        NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 소분류입니다."),
        NOT_FOUND_CATEGORY_CLASS(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 대분류입니다."),
        NOT_FOUND_CART(HttpStatus.NOT_FOUND, "장바구니에 상품 내역이 없습니다."),
        NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "지원하지 않는 로그인입니다."),
        NOT_FOUND_PROVIDER_NUMBER(HttpStatus.NOT_FOUND, "존재하지 않는 Provider Number"),
        NOT_FOUND_ADDRESSES(HttpStatus.NOT_FOUND, "등록된 주소가 없습니다."),
        NOT_FOUND_PRIMARY_ADDRESSES(HttpStatus.NOT_FOUND, "등록된 대표 주소가 없습니다."),
        NOT_FOUND_ADDRESS(HttpStatus.NOT_FOUND, "존재하지 않는 주소입니다."),
        NOT_FOUND_NOTICE(HttpStatus.NOT_FOUND, "존재하지 않는 공지사항입니다."),
        NOT_FOUND_QUESTION(HttpStatus.NOT_FOUND, "존재하지 않는 문의입니다."),
        NOT_FOUND_RESTAURANT_ORDER_NUMBER(HttpStatus.NOT_FOUND, "존재하지 않는 Restaurant Order Number"),
        NOT_FOUND_STATUS(HttpStatus.NOT_FOUND, "존재하지 않는 상태값 입니다."),
        NOT_FOUND_ORDER(HttpStatus.NOT_FOUND, "존재하지 않는 주문입니다."),
        NOT_FOUND_ORDER_DETAIL(HttpStatus.NOT_FOUND, "존재하지 않는 주문상세내역입니다."),
        NOT_FOUND_PAYMENT_CARD_TYPE(HttpStatus.NOT_FOUND, "존재하지 않는 카드 종류 입니다."),
        NOT_FOUND_PAYMENT_METHOD(HttpStatus.NOT_FOUND, "존재하지 않는 결제 수단 입니다."),
        NOT_FOUND_PAYMENT_OWNER_TYPE(HttpStatus.NOT_FOUND, "존재하지 않는 카드의 소유자 타입 입니다."),
        NOT_FOUND_PAYMENT_PROVIDER(HttpStatus.NOT_FOUND, "존재하지 않는 간편결제사 코드 입니다."),
        NOT_FOUND_PAYMENT_STATUS(HttpStatus.NOT_FOUND, "존재하지 않는 결제 처리 상태 입니다."),
        NOT_FOUND_PAYMENT_CARD_CODE(HttpStatus.NOT_FOUND, "존재하지 않는 카드사 코드 입니다."),
        NOT_FOUND_ANSWER(HttpStatus.NOT_FOUND, "존재하지 답변 입니다."),
        NOT_FOUND_REVIEW(HttpStatus.NOT_FOUND, "존재하는 리뷰가 없습니다."),
        NOT_FOUND_RESTAURANT_ORDER_CART(HttpStatus.NOT_FOUND, "카트가 존재하지 않습니다."),
        NOT_FOUND_RESTAURANT_ORDER_CART_DETAIL(HttpStatus.NOT_FOUND, "장바구니에 제품이 존재하지 않습니다. 확인 후 다시 시도해 주세요."),
        NOT_FOUND_RESTAURANT_ORDER_DETAIL(HttpStatus.NOT_FOUND, "발주 상세 내역이 존재하지 않습니다. 확인 후 다시 시도해 주세요."),
        NOT_FOUND_RESTAURANT_STOCK(HttpStatus.NOT_FOUND, "레스토랑 재고가 존재하지 않습니다."),
        NOT_FOUND_RESTAURANT_ORDER(HttpStatus.NOT_FOUND, "상태 변경 가능한 발주 건이 없습니다."),
        NOT_FOUND_STOCK_IN_RESTAURANT(HttpStatus.NOT_FOUND, "재고 항목이 해당 매장에 속하지 않습니다."),

        // 405
        METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "허용되지 않은 HTTP METHOD 입니다."),

        // 409
        CONFLICT(HttpStatus.CONFLICT, "이미 리소스가 존재합니다."),
        MAKER_IN_USE(HttpStatus.CONFLICT, "사용중인 제조사입니다."),
        NICKNAME_IN_USE(HttpStatus.CONFLICT, "사용중인 닉네임입니다."),
        PRODUCT_IN_USE(HttpStatus.CONFLICT, "사용중인 제품입니다."),
        CATEGORY_IN_USE(HttpStatus.CONFLICT, "사용중인 카테고리입니다."),
        ORDER_ALREADY_PAID(HttpStatus.CONFLICT, "이미 결제 완료된 주문입니다."),
        ORDER_ALREADY_CANCEL(HttpStatus.CONFLICT, "이미 취소된 주문입니다."),
        ORDER_ISSUE(HttpStatus.CONFLICT, "주문 처리 중에 문제가 발생 했습니다."),
        DELETED_QUESTION(HttpStatus.CONFLICT, "삭제된 문의사항 입니다."),
        DELETED_ANSWER(HttpStatus.CONFLICT, "삭제 처리가 된 답변 입니다."),
        EXIST_REVIEW(HttpStatus.CONFLICT, "작성된 리뷰가 존재합니다."),
        OUT_OF_ITEM_STOCK(HttpStatus.CONFLICT, "현재 상품에 재고가 없습니다."),
        RESTAURANT_REFUND_FAIL(HttpStatus.CONFLICT, "매장 환불 조건에 부합하지 않습니다."),
        RESTAURANT_REFUND_CANCEL_FAIL(HttpStatus.CONFLICT, "매장 환불 취소 조건에 부합하지 않습니다."),
        RESTAURANT_REFUND_APPROVAL_FAIL(HttpStatus.CONFLICT, "매장 환불 승인 조건에 부합하지 않습니다."),
        RESTAURANT_REFUND_COMPLETE_FAIL(HttpStatus.CONFLICT, "매장 환불 완료 조건에 부합하지 않습니다."),
        RESTAURANT_REFUND_REJECT_FAIL(HttpStatus.CONFLICT, "매장 환불 반려 조건에 부합하지 않습니다."),
        ORDER_CANCEL_FAIL(HttpStatus.CONFLICT, "주문 취소 조건에 부합하지 않습니다."),
        ORDER_CANCEL_COMPLETE_FAIL(HttpStatus.CONFLICT, "주문 취소 완료 조건에 부합하지 않습니다."),
        EXIST_DELETED_DATA(HttpStatus.CONFLICT, "삭제된 필요 데이터가 하나 이상입니다."),

        // 500 서버 에러
        INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러 입니다.");

        private final HttpStatus status;
        private final String message;
    }
}
