package com.drunkenlion.alcoholfriday.domain.admin.api;

import com.drunkenlion.alcoholfriday.domain.admin.application.AdminCustomerService;
import com.drunkenlion.alcoholfriday.domain.admin.application.AdminMemberService;
import com.drunkenlion.alcoholfriday.domain.admin.application.AdminRestaurantService;
import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.dto.MemberListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.dto.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.dto.RestaurantListResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Tag(name = "v1-admin-controller", description = "관리자 컨트롤러")
public class AdminController {
    private final AdminMemberService adminMemberService;
    private final AdminRestaurantService adminRestaurantService;
    private final AdminCustomerService adminCustomerService;

    @Operation(summary = "전체 회원 조회", description = "Admin 권한에 대한 화면 전체 회원 조회")
    @GetMapping(value = "members")
    public ResponseEntity<PageResponse<MemberListResponse>> getMembers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<MemberListResponse> pageResponse = PageResponse.of(this.adminMemberService.getMembers(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "회원 상세 조회")
    @GetMapping(value = "member/{id}")
    public ResponseEntity<MemberDetailResponse> getMember(
            @PathVariable("id") Long id
    ) {
        MemberDetailResponse memberDetailResponse = adminMemberService.getMember(id);
        return ResponseEntity.ok().body(memberDetailResponse);
    }

    @Operation(summary = "전체 매장 조회")
    @GetMapping(value = "restaurants")
    public ResponseEntity<PageResponse<RestaurantListResponse>> getRestaurants(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<RestaurantListResponse> pageResponse = PageResponse.of(this.adminRestaurantService.getRestaurants(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "매장 상세 조회")
    @GetMapping(value = "restaurant/{id}")
    public ResponseEntity<RestaurantDetailResponse> getRestaurant(
            @PathVariable("id") Long id
    ) {
        RestaurantDetailResponse restaurantDetailResponse = adminRestaurantService.getRestaurant(id);
        return ResponseEntity.ok().body(restaurantDetailResponse);
    }

}
