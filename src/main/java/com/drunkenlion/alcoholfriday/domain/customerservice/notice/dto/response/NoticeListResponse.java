package com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "공지사항 목록 조회 항목")
public class NoticeListResponse {
    @Schema(description = "공지사항 고유 식별 번호")
    private Long id;

    @Schema(description = "공지사항 제목")
    private String title;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public static NoticeListResponse of(Notice notice) {
        return NoticeListResponse.builder()
                .id(notice.getId())
                .title(notice.getTitle())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }

    public static Page<NoticeListResponse> of(Page<Notice> notices) {
        return notices.map((NoticeListResponse::of));
    }
}
