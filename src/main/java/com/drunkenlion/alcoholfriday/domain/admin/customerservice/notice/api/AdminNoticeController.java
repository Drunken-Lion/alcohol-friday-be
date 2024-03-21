package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.api;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.application.AdminNoticeService;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.dto.NoticeSaveResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.ncp.application.NcpS3Service;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/admin/notices")
@Tag(name = "v1-admin-notice", description = "관리자용 공지사항 API")
@SecurityRequirement(name = "bearerAuth")
@RestController
public class AdminNoticeController {

    private final AdminNoticeService adminNoticeService;
    private final NcpS3Service ncpS3Service;

    @Operation(summary = "공지사항 상세 조회", description = "관리자 권한 - 공지사항 상세 조회")
    @GetMapping("{id}")
    public ResponseEntity<NoticeSaveResponse> getNotice(@PathVariable("id") Long id,
                                                        @AuthenticationPrincipal UserPrincipal user) {
        NoticeSaveResponse noticeSaveResponse = adminNoticeService.getNotice(id, user.getMember());
      
        return ResponseEntity.ok().body(noticeSaveResponse);
    }

    @Operation(summary = "공지사항 목록 조회", description = "관리자 권한 - 공지사항 목록 조회, 검색")
    @GetMapping
    public ResponseEntity<PageResponse<NoticeSaveResponse>> getNotices(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "keywordType", defaultValue = "") @Schema(example = "title,content") String keywordType,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal UserPrincipal user) {
        List<String> parseType = List.of(keywordType.split(","));
        PageResponse<NoticeSaveResponse> noticeSaveResponse = PageResponse.of(adminNoticeService.getNotices(page, size, user.getMember(), keyword, parseType));

        return ResponseEntity.ok().body(noticeSaveResponse);
    }

    @Operation(summary = "공지사항 초기화", description = "관리자 권한 - 공지사항 초기 빈 객체 생성")
    @PostMapping
    public ResponseEntity<NoticeSaveResponse> initNotice(@AuthenticationPrincipal UserPrincipal user) {
        NoticeSaveResponse initResponse = adminNoticeService.initNotice(user.getMember());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(initResponse.getId())
                .toUri();
      
        return ResponseEntity.created(location).body(initResponse);
    }

    @Operation(summary = "공지사항 등록, 수정", description = "관리자 권한 - 공지사항 등록, 수정")
    @PutMapping("{id}")
    public ResponseEntity<NoticeSaveResponse> modifyNotice(@PathVariable("id") Long id,
                                                           @RequestBody @Valid NoticeSaveRequest request,
                                                           @AuthenticationPrincipal UserPrincipal user) {
        NoticeSaveResponse response = adminNoticeService.modifyNotice(id, request, user.getMember());
      
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "공지사항 삭제", description = "관리자 권한 - 공지사항 삭제")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteNotice(@PathVariable("id") Long id,
                                             @AuthenticationPrincipal UserPrincipal user) {
        adminNoticeService.deleteNotice(id, user.getMember());
      
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "공지사항 이미지 등록", description = "관리자 권한 - 공지사항 작성 중 이미지 등록")
    @PostMapping("{id}")
    public ResponseEntity<String> saveNoticeImage(@PathVariable("id") Long id,
                                                  @RequestPart("file")  MultipartFile file) {
        String response = ncpS3Service.saveFile(id, file);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
      
        return ResponseEntity.created(location).body(response);
    }
}