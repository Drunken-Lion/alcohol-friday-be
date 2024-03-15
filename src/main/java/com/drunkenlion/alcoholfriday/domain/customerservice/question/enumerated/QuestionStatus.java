package com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum QuestionStatus {
    COMPLETE("완료"),
    INCOMPLETE("미완료");

    private final String label;

    QuestionStatus(String status) {
        this.label = status;
    }

    public static QuestionStatus ofStatus(String status) {
        return Arrays.stream(QuestionStatus.values())
                .filter(value -> value.label.equals(status))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND)
                        .build());
    }
}
