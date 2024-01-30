package com.drunkenlion.alcoholfriday.global.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class Code {
    @Getter
    @RequiredArgsConstructor
    public enum Success {
        // 200
        OK(200),
        CREATED(201),
        NO_CONTENT(204),

        // 300
        MOVED_PERMANENTLY(301),
        FOUND(302),
        NOT_MODIFIDED(304),
        TEMPORARY_REDIRECT(307),
        PERMANENT_REDIRECT(308);

        private final int status;
    }

    @Getter
    @RequiredArgsConstructor
    public enum Fail {
        // 400
        BAD_REQUEST(400),
        INVALID_INPUT_VALUE(400),

        // 401
        UNAUTHORIZED(401),
        INVALID_TOKEN(401),
        EXPIRED_TOKEN(401),
        UNAUTHORIZED_TOKEN(401),
        WRONG_TOKEN(401),
        INVALID_ACCOUNT(401),

        // 403
        FORBIDDEN(403),
        DEACTIVATE_USER(403),

        // 404
        NOT_FOUND_OBJECT(404),

        // 405
        METHOD_NOT_ALLOWED(405),

        // 409
        CONFLICT(409),

        // 500 서버 에러
        INTERNAL_SERVER_ERROR(500);

        private final int status;
    }
}