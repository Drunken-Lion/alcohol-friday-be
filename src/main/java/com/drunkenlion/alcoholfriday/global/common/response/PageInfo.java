package com.drunkenlion.alcoholfriday.global.common.response;

import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class PageInfo {
    private int size;
    private Long count;

    public static PageInfo of(Page page) {
        return PageInfo.builder()
                .size(page.getPageable().getPageSize())
                .count(page.getTotalElements())
                .build();
    }
}
