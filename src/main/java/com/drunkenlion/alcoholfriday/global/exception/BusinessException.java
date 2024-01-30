package com.drunkenlion.alcoholfriday.global.exception;

import com.drunkenlion.alcoholfriday.global.common.response.Code;
import com.drunkenlion.alcoholfriday.global.common.response.Message;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BusinessException extends RuntimeException {
    private int status;
    private String message;

    @Builder
    public BusinessException(Code.Fail code, Message.Fail message) {
        super(message.getMessage());
        this.status = code.getStatus();
        this.message = message.getMessage();
    }
}
