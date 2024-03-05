package com.drunkenlion.alcoholfriday.domain.customerservice.api;

import com.drunkenlion.alcoholfriday.domain.customerservice.application.QuestionService;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.request.QuestionSaveRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/questions")
@Tag(name = "v1-question", description = "문의사항 API")
@RestController
@SecurityRequirement(name = "bearerAuth")
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping
    @Operation(summary = "문의사항 등록")
    public ResponseEntity<QuestionSaveResponse> saveQuestion(@RequestPart @Valid QuestionSaveRequest request,
                                                             @RequestPart List<MultipartFile> files,
                                                             @AuthenticationPrincipal UserPrincipal user) {
        QuestionSaveResponse response = questionService.saveQuestion(request, files, user.getMember());
        return ResponseEntity.ok(response);
    }
}
