package com.drunkenlion.alcoholfriday.domain.member.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ReviewStatus {
    PENDING("pending"),
    COMPLETE("complete");

    private final String status;

    ReviewStatus(String status) {
        this.status = status;
    }

    public static ReviewStatus of(String status) {
        return Arrays.stream(ReviewStatus.values())
                .filter(value -> value.status.equals(status))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND)
                        .build());
    }
}
