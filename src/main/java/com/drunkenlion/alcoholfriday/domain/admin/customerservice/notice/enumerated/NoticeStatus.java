package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NoticeStatus {
    DRAFT("작성 중"),
    PUBLISHED("작성 완료");

    private final String label;

    NoticeStatus(String status) { this.label = status; }

    public static NoticeStatus ofStatus(String status) {
        return Arrays.stream(NoticeStatus.values())
                .filter(value -> value.label.equals(status))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND)
                        .build());
    }
}
