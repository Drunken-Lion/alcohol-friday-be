package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "공지사항 응답")
public class NoticeSaveResponse {
    @Schema(description = "공지사항 고유 식별 번호")
    private Long id;

    @Schema(description = "공지사항 작성자")
    private MemberResponse member;

    @Schema(description = "공지사항 제목")
    private String title;

    @Schema(description = "공지사항 내용")
    private String content;

    @Schema(description = "공지사항 작성 상태")
    private NoticeStatus status;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    public static NoticeSaveResponse of(Notice notice) {
        return NoticeSaveResponse.builder()
                .id(notice.getId())
                .member(MemberResponse.of(notice.getMember()))
                .title(notice.getTitle())
                .content(notice.getContent())
                .status(notice.getStatus())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .deletedAt(notice.getDeletedAt())
                .build();
    }
}
