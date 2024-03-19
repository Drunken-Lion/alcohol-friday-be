package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberQuestionListResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberResponse;
import com.drunkenlion.alcoholfriday.domain.member.dto.MemberReviewResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.ReviewStatus;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.dto.response.ReviewResponse;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final AddressRepository addressRepository;
    private final ReviewRepository reviewRepository;

    private final FileService fileService;

    @Transactional
    @Override
    public MemberResponse modifyMember(Member member, MemberModifyRequest modifyRequest) {
        if (memberRepository.existsByNickname(modifyRequest.getNickname())) {
            throw new BusinessException(HttpResponse.Fail.NICKNAME_IN_USE);
        }

        member = member.toBuilder()
                .nickname(modifyRequest.getNickname())
                .phone(modifyRequest.getPhone())
                .build();

        return MemberResponse.of(memberRepository.save(member));
    }

    @Override
    public Page<MemberQuestionListResponse> getMyQuestions(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Question> questionPage = questionRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable);

        return questionPage.map(MemberQuestionListResponse::of);
    }

    @Override
    public Page<OrderResponse> getMyOrders(Member member, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Order> orderPage = orderRepository.findMyOrderList(member, pageable);

        return orderPage.map(order -> {
            List<OrderDetailResponse> orderDetailsResponses =
                    order.getOrderDetails().stream().map(orderDetail ->
                            OrderDetailResponse.of(orderDetail, fileService.findOne(orderDetail.getItem()))).toList();

            return OrderResponse.of(order, orderDetailsResponses);
        });
    }

    @Override
    public List<AddressResponse> getMyAddresses(Long memberId) {
        List<Address> addresses = addressRepository.findAllByMemberIdOrderByIsPrimaryDescCreatedAtDesc(memberId);

        if (addresses.isEmpty()) throw new BusinessException(HttpResponse.Fail.NOT_FOUND_ADDRESSES);

        return addresses.stream().map(AddressResponse::of).toList();
    }

    @Override
    public Page<MemberReviewResponse<?>> getMyReviews(Long memberId, ReviewStatus reviewStatus, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (reviewStatus == ReviewStatus.PENDING) {
            return pendingReviews(memberId, pageable).map(
                    response -> MemberReviewResponse.of(reviewStatus.getStatus(), response));
        }

        return writtenReviews(memberId, pageable).map(
                response -> MemberReviewResponse.of(reviewStatus.getStatus(), response));
    }

    private Page<OrderDetailResponse> pendingReviews(Long memberId, Pageable pageable) {
        Page<OrderDetail> orderDetails = orderDetailRepository.findByOrderMemberIdAndReviewIsNull(memberId, pageable);
        return orderDetails.map(OrderDetailResponse::of);
    }

    private Page<ReviewResponse> writtenReviews(Long memberId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAllByMemberIdAndDeletedAtIsNull(memberId, pageable);
        return reviews.map(ReviewResponse::of);
    }
}
