package com.drunkenlion.alcoholfriday.domain.customerservice.api;

import com.drunkenlion.alcoholfriday.domain.customerservice.application.QuestionService;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.request.QuestionModifyRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.request.QuestionSaveRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.response.QuestionResponse;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RequiredArgsConstructor
@RequestMapping("/v1/questions")
@Tag(name = "v1-question", description = "문의사항 API")
@RestController
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    @Operation(summary = "문의사항 등록")
    public ResponseEntity<QuestionSaveResponse> saveQuestion(@Valid @RequestPart("request") QuestionSaveRequest request,
                                                             @RequestPart("files") List<MultipartFile> files,
                                                             @AuthenticationPrincipal UserPrincipal user) {
        QuestionSaveResponse response = questionService.saveQuestion(request, files, user.getMember());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }


    @GetMapping
    @Operation(summary = "문의사항 전체 조회", description = "로그인 회원이 작성한 문의사항 전체 조회")
    public ResponseEntity<?> get(@RequestParam(name = "page", defaultValue = "0") int page,
                                 @RequestParam(name = "size", defaultValue = "20") int size,
                                 @AuthenticationPrincipal UserPrincipal user) {
        Page<QuestionResponse> all = questionService.findAll(user.getMember(), page, size);
        return ResponseEntity.ok(all);
    }

    @GetMapping("{id}")
    @Operation(summary = "문의사항 상세 조회")
    public ResponseEntity<QuestionResponse> get(@PathVariable("id") Long id,
                                                @AuthenticationPrincipal UserPrincipal user) {
        QuestionResponse response = questionService.findQuestion(user.getMember(), id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("{id}")
    @Operation(summary = "문의사항 수정")
    public ResponseEntity<QuestionResponse> update(@PathVariable("id") Long id,
                                                   @RequestPart QuestionModifyRequest request,
                                                   @RequestPart List<MultipartFile> files,
                                                   @AuthenticationPrincipal UserPrincipal user) {

        QuestionResponse questionResponse = questionService.updateQuestion(id, user.getMember(), request, files);
        return ResponseEntity.ok(questionResponse);
    }

    @DeleteMapping("{id}")
    @Operation(summary = "문의사항 삭제")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id,
                                       @AuthenticationPrincipal UserPrincipal user) {
        questionService.deleteQuestion(id, user.getMember());
        return ResponseEntity.noContent().build();
    }
}
