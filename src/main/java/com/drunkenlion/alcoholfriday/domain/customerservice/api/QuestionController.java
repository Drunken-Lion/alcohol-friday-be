package com.drunkenlion.alcoholfriday.domain.customerservice.api;

import com.drunkenlion.alcoholfriday.domain.customerservice.application.QuestionService;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.request.QuestionRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.dto.response.QuestionSaveResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/v1/cs/question")
@RestController
public class QuestionController {
    private final QuestionService questionService;

    @PostMapping("/save")
    @Operation(summary = "문의사항 등록")
    public ResponseEntity<QuestionSaveResponse> saveQuestion(QuestionRequest questionRequest,
                                                            @AuthenticationPrincipal UserPrincipal user) {
        QuestionSaveResponse response = questionService.saveQuestion(questionRequest, user.getMember());
        return ResponseEntity.ok(response);
    }
}
