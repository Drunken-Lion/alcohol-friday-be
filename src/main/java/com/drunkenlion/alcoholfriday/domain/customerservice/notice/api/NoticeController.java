package com.drunkenlion.alcoholfriday.domain.customerservice.notice.api;

import com.drunkenlion.alcoholfriday.domain.customerservice.notice.application.NoticeService;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeDetailResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dto.response.NoticeListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/notices")
@Tag(name = "v1-notice", description = "일반 사용자용 공지사항 API")
@RestController
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지사항 상세 조회", description = "일반 사용자용 공지사항 상세 조회")
    @GetMapping("{id}")
    public ResponseEntity<NoticeDetailResponse> getNotice(
            @PathVariable("id") Long id) {
        NoticeDetailResponse noticeDetailResponse = noticeService.getNotice(id);
      
        return ResponseEntity.ok().body(noticeDetailResponse);
    }

    @Operation(summary = "공지사항 목록 조회", description = "일반 사용자용 공지사항 목록 조회, 검색")
    @GetMapping
    public ResponseEntity<PageResponse<NoticeListResponse>> getNotices(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "keywordType", defaultValue = "") @Schema(example = "title,content") String keywordType,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        List<String> parseType = List.of(keywordType.split(","));
        PageResponse<NoticeListResponse> noticeListResponse = PageResponse.of(noticeService.getNotices(page, size, keyword, parseType));
      
        return ResponseEntity.ok().body(noticeListResponse);
    }
}
