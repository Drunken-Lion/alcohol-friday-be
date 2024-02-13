package com.drunkenlion.alcoholfriday.domain.member.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.drunkenlion.alcoholfriday.domain.member.application.MemberService;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
@Tag(name = "v1-members-controller", description = "회원 관련 컨트롤러")
public class MemberController {
	private final MemberService memberService;
	
	@PostMapping("/test")
	public ResponseEntity<?> authMemberTest(@AuthenticationPrincipal UserPrincipal userPrincipal) {
		MemberResponse memberResponse = memberService.getMember(userPrincipal.getUsername());
		return ResponseEntity.ok().body(memberResponse);
	}
}
