package com.drunkenlion.alcoholfriday.domain.member.api;

import com.drunkenlion.alcoholfriday.domain.member.application.MemberService;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
@Tag(name = "v1-members", description = "회원 관련 API")
public class MemberController {
    private final MemberService memberService;

    @GetMapping
    public ResponseEntity<MemberResponse> getMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Optional.ofNullable(userPrincipal)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.UNAUTHORIZED));

        MemberResponse memberResponse = memberService.getMember(userPrincipal.getUsername());
        return ResponseEntity.ok().body(memberResponse);
    }
}
