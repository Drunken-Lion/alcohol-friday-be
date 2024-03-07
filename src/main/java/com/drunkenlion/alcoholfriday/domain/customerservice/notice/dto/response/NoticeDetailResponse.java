package com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "공지사항 상세 조회 항목")
public class NoticeDetailResponse {
    @Schema(description = "공지사항 고유 식별 번호")
    private Long id;

    @Schema(description = "공지사항 작성자")
    private MemberResponse member;

    @Schema(description = "공지사항 제목")
    private String title;

    @Schema(description = "공지사항 내용")
    private String content;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    public static NoticeDetailResponse of(Notice notice) {
        return NoticeDetailResponse.builder()
                .id(notice.getId())
                .member(MemberResponse.of(notice.getMember()))
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
