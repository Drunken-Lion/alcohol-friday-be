package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.api;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.application.AdminAnswerService;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.request.AnswerSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response.AdminAnswerResponse;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.dto.response.AdminAnswerSaveResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "v1-admin-answer", description = "관리자 문의사항 관리 API")
@RequestMapping("/v1/admin/answers")
@RestController
public class AdminAnswerController {
    private final AdminAnswerService adminAnswerService;

    @PostMapping
    @Operation(summary = "문의사항 답변 등록")
    public ResponseEntity<AdminAnswerSaveResponse> saveAnswer(@Valid @RequestBody AnswerSaveRequest request,
                                                              @AuthenticationPrincipal UserPrincipal user) {
        AdminAnswerSaveResponse response = adminAnswerService.saveAnswer(request, user.getMember());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @PutMapping("{id}")
    @Operation(summary = "문의사항 답변 수정")
    public ResponseEntity<AdminAnswerResponse> updateAnswer(@PathVariable("id") Long id,
                                          @RequestBody AnswerModifyRequest request,
                                          @AuthenticationPrincipal UserPrincipal user) {
        AdminAnswerResponse response = adminAnswerService.updateAnswer(id, request, user.getMember());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "문의사항 답변 삭제")
    public ResponseEntity<Void> deleteAnswer(@PathVariable("id") Long id,
                                             @AuthenticationPrincipal UserPrincipal user) {
        adminAnswerService.deleteAnswer(id, user.getMember());
        return ResponseEntity.noContent().build();
    }
}
