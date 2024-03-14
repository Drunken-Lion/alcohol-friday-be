package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.*;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.ReviewStatus;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MemberService {
    MemberResponse modifyMember(Member member, MemberModifyRequest modifyRequest);

    Page<MemberQuestionListResponse> getMyQuestions(Long memberId, int page, int size);

    Page<OrderResponse> getMyOrders(Member member, int page, int size);

    List<AddressResponse> getMyAddresses(Long memberId);

    Page<MemberReviewResponse<?>> getMyReviews(Long memberId, ReviewStatus reviewStatus, int page, int size);
}
