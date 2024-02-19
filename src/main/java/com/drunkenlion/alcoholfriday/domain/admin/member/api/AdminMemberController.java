package com.drunkenlion.alcoholfriday.domain.admin.member.api;

import com.drunkenlion.alcoholfriday.domain.admin.member.application.AdminMemberService;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Tag(name = "v1-admin-member", description = "관리자 회원관리에 대한 API")
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    @Operation(summary = "전체 회원 조회", description = "관리자 권한에 대한 전체 회원 조회")
    @GetMapping(value = "members")
    public ResponseEntity<PageResponse<MemberListResponse>> getMembers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<MemberListResponse> pageResponse = PageResponse.of(this.adminMemberService.getMembers(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "회원 상세 조회", description = "관리자 화면에 대한 회원 상세 조회")
    @GetMapping(value = "members/{id}")
    public ResponseEntity<MemberDetailResponse> getMember(
            @PathVariable("id") Long id
    ) {
        MemberDetailResponse memberDetailResponse = adminMemberService.getMember(id);
        return ResponseEntity.ok().body(memberDetailResponse);
    }

    @Operation(summary = "회원 수정", description = "관리자 화면에 대한 회원 수정")
    @PutMapping(value = "members/{id}")
    public ResponseEntity<MemberDetailResponse> modifyMember(
            @PathVariable("id") Long id,
            @Valid @RequestBody MemberModifyRequest memberModifyRequest
    ) {
        MemberDetailResponse memberDetailResponse = adminMemberService.modifyMember(id, memberModifyRequest);
        return ResponseEntity.ok().body(memberDetailResponse);
    }
}
