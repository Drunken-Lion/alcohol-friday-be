package com.drunkenlion.alcoholfriday.domain.member.api;

import com.drunkenlion.alcoholfriday.domain.member.application.MemberService;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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

    @Operation(summary = "회원 정보 조회", description = "마이페이지 회원 정보 조회")
    @GetMapping
    public ResponseEntity<MemberResponse> getMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        validUserPrincipal(userPrincipal);

        MemberResponse memberResponse = memberService.getMember(userPrincipal.getUsername());
        return ResponseEntity.ok().body(memberResponse);
    }

    @Operation(summary = "회원 정보 수정", description = "마이페이지 회원 정보 수정")
    @PutMapping
    public ResponseEntity<MemberResponse> modifyMember(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @RequestBody MemberModifyRequest modifyRequest) {
        validUserPrincipal(userPrincipal);

        MemberResponse memberResponse = memberService.modifyMember(userPrincipal.getUsername(), modifyRequest);
        return ResponseEntity.ok().body(memberResponse);
    }

    private void validUserPrincipal(UserPrincipal userPrincipal) {
        Optional.ofNullable(userPrincipal)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.UNAUTHORIZED));
    }
}
