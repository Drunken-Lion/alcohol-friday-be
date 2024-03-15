package com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.api;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.application.AdminQuestionService;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.dto.response.AdminQuestionResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "v1-admin-question", description = "관리자 문의사항 관리 API")
@RequestMapping("/v1/admin/questions")
@RestController
public class AdminQuestionController {
    private final AdminQuestionService adminQuestionService;

    @GetMapping
    @Operation(summary = "관리자 문의사항 전체 조회")
    public ResponseEntity<PageResponse<AdminQuestionResponse>> findQuestions(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                             @RequestParam(name = "size", defaultValue = "10") int size,
                                                                             @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<AdminQuestionResponse> findAll = PageResponse.of(adminQuestionService.findQuestions(user.getMember(), page, size));
        return ResponseEntity.ok(findAll);
    }

    @GetMapping("{id}")
    @Operation(summary = "관리자 문의사항 상세 조회")
    public ResponseEntity<AdminQuestionResponse> findQuestion(@PathVariable("id") Long id,
                                                         @AuthenticationPrincipal UserPrincipal user) {
        AdminQuestionResponse response = adminQuestionService.findQuestion(user.getMember(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}")
    @Operation(summary = "관리자 문의사항 수정")
    public ResponseEntity<AdminQuestionResponse> update(@PathVariable("id") Long id,
                                                   @RequestPart("request") QuestionModifyRequest request,
                                                   @RequestPart("files") List<MultipartFile> files,
                                                   @AuthenticationPrincipal UserPrincipal user) {
        AdminQuestionResponse questionResponse = adminQuestionService.updateQuestion(id, user.getMember(), request, files);
        return ResponseEntity.ok(questionResponse);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "관리자 문의사항 삭제")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal UserPrincipal user) {
        adminQuestionService.deleteQuestion(id, user.getMember());
        return ResponseEntity.noContent().build();
    }
}
