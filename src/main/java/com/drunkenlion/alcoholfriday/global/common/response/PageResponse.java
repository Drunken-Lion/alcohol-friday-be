package com.drunkenlion.alcoholfriday.global.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "페이징이 적용된 응답")
public class PageResponse<T> {
    private T data;
    private PageInfo pageInfo;

    /**
     * 여러 DTO를 data로 넣어주기 위해선 원시타입으로밖에 사용할 수 없음...
     */
    public static <T> PageResponse of(Page<T> page) {
        return PageResponse.builder()
                .data(page.getContent())
                .pageInfo(PageInfo.of(page))
                .build();
    }
}
