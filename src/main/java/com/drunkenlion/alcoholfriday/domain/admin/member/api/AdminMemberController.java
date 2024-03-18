package com.drunkenlion.alcoholfriday.domain.admin.member.api;

import com.drunkenlion.alcoholfriday.domain.admin.member.application.AdminMemberService;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/members")
@Tag(name = "v1-admin-member", description = "관리자 회원 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    @Operation(summary = "전체 회원 조회", description = "관리자 권한에 대한 전체 회원 조회")
    @GetMapping
    public ResponseEntity<PageResponse<MemberListResponse>> getMembers(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<MemberListResponse> pageResponse = PageResponse.of(this.adminMemberService.getMembers(userPrincipal.getMember(), page, size));

        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "회원 상세 조회", description = "관리자 화면에 대한 회원 상세 조회")
    @GetMapping("{id}")
    public ResponseEntity<MemberDetailResponse> getMember(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id
    ) {
        MemberDetailResponse memberDetailResponse = adminMemberService.getMember(userPrincipal.getMember(), id);
        return ResponseEntity.ok().body(memberDetailResponse);
    }

    @Operation(summary = "회원 수정", description = "관리자 화면에 대한 회원 수정")
    @PutMapping("{id}")
    public ResponseEntity<MemberDetailResponse> modifyMember(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable("id") Long id,
            @Valid @RequestBody MemberModifyRequest memberModifyRequest
    ) {
        MemberDetailResponse memberDetailResponse = adminMemberService.modifyMember(userPrincipal.getMember(), id, memberModifyRequest);
        return ResponseEntity.ok().body(memberDetailResponse);
    }
}
