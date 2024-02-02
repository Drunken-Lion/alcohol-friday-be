package com.drunkenlion.alcoholfriday.domain.admin.api;

import com.drunkenlion.alcoholfriday.domain.admin.application.AdminCustomerService;
import com.drunkenlion.alcoholfriday.domain.admin.application.AdminMemberService;
import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class AdminController {
    private final AdminMemberService adminMemberService;
    private final AdminCustomerService adminCustomerService;

    @GetMapping(value = "members")
    public ResponseEntity<PageResponse<MemberListResponse>> getMembers() {
        PageResponse<MemberListResponse> pageResponse = PageResponse.of(this.adminMemberService.getMembers());
        return ResponseEntity.ok().body(pageResponse);
    }
}
