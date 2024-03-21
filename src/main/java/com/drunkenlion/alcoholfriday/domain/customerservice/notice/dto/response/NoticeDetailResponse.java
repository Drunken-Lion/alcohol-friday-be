package com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
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

    @Schema(description = "공지사항 작성자 ID")
    private Long memberId;

    @Schema(description = "공지사항 작성자 이름")
    private String memberName;

    @Schema(description = "공지사항 작성자 닉네임")
    private String memberNickname;

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
                .memberId(notice.getMember().getId())
                .memberName(notice.getMember().getName())
                .memberNickname(notice.getMember().getNickname())
                .title(notice.getTitle())
                .content(notice.getContent())
                .createdAt(notice.getCreatedAt())
                .updatedAt(notice.getUpdatedAt())
                .build();
    }
}
