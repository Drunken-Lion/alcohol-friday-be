package com.drunkenlion.alcoholfriday.domain.admin.api;

import com.drunkenlion.alcoholfriday.domain.admin.application.AdminCustomerService;
import com.drunkenlion.alcoholfriday.domain.admin.application.AdminMemberService;
import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {
    private final AdminMemberService adminMemberService;
    private final AdminCustomerService adminCustomerService;

    @GetMapping(value = "members")
    public ResponseEntity<PageResponse<MemberListResponse>> getMembers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<MemberListResponse> pageResponse = PageResponse.of(this.adminMemberService.getMembers(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @GetMapping(value = "member/{id}")
    public ResponseEntity<MemberDetailResponse> getMember(
            @PathVariable("id") Long id
    ) {
        MemberDetailResponse memberDetailResponse = adminMemberService.getMember(id);
        return ResponseEntity.ok().body(memberDetailResponse);
    }
}
