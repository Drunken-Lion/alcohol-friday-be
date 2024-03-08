package com.drunkenlion.alcoholfriday.domain.member.api;

import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.member.application.MemberService;
import com.drunkenlion.alcoholfriday.domain.member.dto.*;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.ReviewStatus;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
@Tag(name = "v1-members", description = "회원 관련 API")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원 정보 조회", description = "마이페이지 회원 정보 조회")
    @GetMapping("me")
    public ResponseEntity<MemberResponse> getAuthMember(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        MemberResponse memberResponse = MemberResponse.of(userPrincipal.getMember());
        return ResponseEntity.ok().body(memberResponse);
    }

    @Operation(summary = "회원 정보 수정", description = "마이페이지 회원 정보 수정")
    @PutMapping("me")
    public ResponseEntity<MemberResponse> modifyMember(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                                       @RequestBody MemberModifyRequest modifyRequest) {
        MemberResponse memberResponse = memberService.modifyMember(userPrincipal.getMember(), modifyRequest);
        return ResponseEntity.ok().body(memberResponse);
    }

    @Operation(summary = "나의 문의 내역", description = "내가 쓴 문의 목록")
    @GetMapping("me/questions")
    public ResponseEntity<PageResponse<MemberQuestionListResponse>> getMyQuestions(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<MemberQuestionListResponse> pageQuestions = memberService.getMyQuestions(userPrincipal.getMember().getId(), page, size);
        PageResponse<MemberQuestionListResponse> pageResponse = PageResponse.of(pageQuestions);

        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "나의 주문 내역", description = "내가 주문한 내역 목록")
    @GetMapping("me/orders")
    public ResponseEntity<PageResponse<OrderResponse>> getMyOrders(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<OrderResponse> pageOrders = memberService.getMyOrders(userPrincipal.getMember().getId(), page, size);
        PageResponse<OrderResponse> pageResponse = PageResponse.of(pageOrders);

        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "나의 배송지 목록", description = "내가 등록한 배송지 목록 (최대3개)")
    @GetMapping("me/addresses")
    public ResponseEntity<List<AddressResponse>> getMyAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<AddressResponse> addressResponses = memberService.getMyAddresses(userPrincipal.getMember().getId());
        return ResponseEntity.ok().body(addressResponses);
    }

    @Operation(summary = "나의 리뷰 목록", description = "내가 쓸 or 쓴 리뷰 목록")
    @GetMapping("me/reviews")
    public ResponseEntity<PageResponse<MemberReviewResponse<?>>> getMyReviews(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam(name = "status", defaultValue = "pending") ReviewStatus reviewStatus,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        Page<MemberReviewResponse<?>> pageReviews = memberService.getMyReviews(
                userPrincipal.getMember().getId(), reviewStatus, page, size);

        PageResponse<MemberReviewResponse<?>> pageResponse = PageResponse.of(pageReviews);

        return ResponseEntity.ok().body(pageResponse);
    }
}
