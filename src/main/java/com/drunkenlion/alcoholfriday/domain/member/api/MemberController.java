package com.drunkenlion.alcoholfriday.domain.member.api;

import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.member.application.MemberService;
import com.drunkenlion.alcoholfriday.domain.member.dto.*;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.ReviewStatus;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import com.drunkenlion.alcoholfriday.domain.review.application.ReviewService;
import com.drunkenlion.alcoholfriday.domain.review.dto.request.ReviewSaveRequest;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewModifyRequest;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewModifyResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewSaveResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.drunkenlion.alcoholfriday.global.security.auth.UserPrincipal;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/members")
@Tag(name = "v1-members", description = "회원 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class MemberController {
    private final MemberService memberService;
    private final ReviewService reviewService;

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

        Page<OrderResponse> pageOrders = memberService.getMyOrders(userPrincipal.getMember(), page, size);
        PageResponse<OrderResponse> pageResponse = PageResponse.of(pageOrders);

        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "나의 배송지 목록", description = "내가 등록한 배송지 목록 (최대3개)")
    @GetMapping("me/addresses")
    public ResponseEntity<List<AddressResponse>> getMyAddresses(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<AddressResponse> addressResponses = memberService.getMyAddresses(userPrincipal.getMember().getId());
        return ResponseEntity.ok().body(addressResponses);
    }

    @PostMapping("me/reviews")
    @Operation(summary = "리뷰 등록")
    public ResponseEntity<ReviewSaveResponse> saveReview(@Valid @RequestPart("request") ReviewSaveRequest request,
                                                         @RequestPart("files") List<MultipartFile> files,
                                                         @AuthenticationPrincipal UserPrincipal user) {
        ReviewSaveResponse response = reviewService.saveReview(request, files, user.getMember());
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("me/reviews")
    @Operation(summary = "작성한 리뷰 목록", description = "내가 쓴 리뷰 목록")
    public ResponseEntity<PageResponse<ReviewOrderDetailResponse>> getReviews(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                              @RequestParam(name = "size", defaultValue = "10") int size,
                                                                              @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<ReviewOrderDetailResponse> response = PageResponse.of(reviewService.getReviews(user.getMember(), page, size));
        return ResponseEntity.ok(response);
    }

    @GetMapping("me/reviews/unwritten")
    @Operation(summary = "작성하지 않은 리뷰 목록")
    public ResponseEntity<PageResponse<ReviewOrderDetailResponse>> getUnwrittenReviews(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                                       @RequestParam(name = "size", defaultValue = "10") int size,
                                                                                       @AuthenticationPrincipal UserPrincipal user) {
        PageResponse<ReviewOrderDetailResponse> response = PageResponse.of(reviewService.getUnwrittenReviews(user.getMember(), page, size));
        return ResponseEntity.ok(response);
    }

    @PutMapping("me/reviews/{id}")
    @Operation(summary = "리뷰 수정")
    public ResponseEntity<ReviewModifyResponse> updateReview(@PathVariable("id") Long id,
                                                             @RequestPart("request") ReviewModifyRequest request,
                                                             @RequestPart("files") List<MultipartFile> files,
                                                             @AuthenticationPrincipal UserPrincipal user) {
        ReviewModifyResponse response = reviewService.updateReview(id, request, user.getMember(), files);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("me/reviews/{id}")
    @Operation(summary = "리뷰 삭제")
    public ResponseEntity<Void> deleteReview(@PathVariable("id") Long id,
                                             @AuthenticationPrincipal UserPrincipal user) {
        reviewService.deleteReview(id, user.getMember());
        return ResponseEntity.noContent().build();
    }
}
