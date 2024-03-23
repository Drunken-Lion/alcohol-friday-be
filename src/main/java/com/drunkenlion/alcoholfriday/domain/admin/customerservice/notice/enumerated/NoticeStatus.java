package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum NoticeStatus {
    DRAFT("작성 중", "1"),
    PUBLISHED("작성 완료", "2");

    private final String status;
    private final String statusNumber;

    NoticeStatus(String status, String statusNumber) {
        this.status = status;
        this.statusNumber = statusNumber;
    }

    public static NoticeStatus byStatus(String status) {
        return Arrays.stream(NoticeStatus.values())
                .filter(value -> value.getStatus().equals(status))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_STATUS)
                        .build());
    }

    public static NoticeStatus byStatusNumber(String statusNumber) {
        return Arrays.stream(NoticeStatus.values())
                .filter(value -> value.getStatusNumber().equals(statusNumber))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_STATUS)
                        .build());
    }
}
