package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.dto.AddressResponse;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.dto.*;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.ReviewStatus;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.order.dto.OrderResponse;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.dto.ReviewResponse;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class MemberServiceTest {
    @InjectMocks
    private MemberServiceImpl memberService;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private ReviewRepository reviewRepository;

    private final Long memberId = 1L;
    private final String email = "test@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String name = "테스트";
    private final String nickname = "test";
    private final String role = MemberRole.MEMBER.getRole();
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = null;
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;

    private final String modifyNickname = "수정테스트";
    private final Long modifyPhone = 1011112222L;

    private final Long questionId = 1L;
    private final String title = "문의 제목1";
    private final String content = "문의 내용1";
    private final String questionStatus = QuestionStatus.COMPLETE.getLabel();

    private final String itemName = "테스트 술";
    private final BigDecimal itemPrice = BigDecimal.valueOf(50000);
    private final String itemInfo = "이 상품은 테스트 상품입니다.";

    private final Long quantity = 2L;
    private final BigDecimal totalPrice = BigDecimal.valueOf(itemPrice.intValue() * quantity);

    private final Long orderId = 1L;
    private final String orderNo = "order_" + orderId;
    private final String orderStatus = OrderStatus.PAYMENT_COMPLETED.name();
    private final BigDecimal orderPrice = BigDecimal.valueOf(100000);
    private final String recipient = "테스트";
    private final Long recipientPhone = 1012345678L;
    private final String description = "부재시 연락주세요.";

    private final Long orderDetailId = 1L;

    private final Long addressId = 1L;
    private final Boolean isPrimary = true;

    private final String address = "서울시 마포구 연남동";
    private final String addressDetail = "123-12";
    private final Long postcode = 123123L;

    private final Long reviewId = 1L;
    private final Long score = 5L;
    private final String reviewContent = "맛있어요.";
    private final String pendingStatus = ReviewStatus.PENDING.getStatus();
    private final String completeStatus = ReviewStatus.COMPLETE.getStatus();

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = null;
    private final LocalDateTime deletedAt = null;

    private final int page = 0;
    private final int size = 5;

    @Test
    @DisplayName("회원 정보 수정")
    public void modifyMemberTest() {
        // given
        MemberModifyRequest memberModifyRequest = MemberModifyRequest.builder()
                .nickname(modifyNickname)
                .phone(modifyPhone)
                .build();

        // When
        Member member = Member.builder()
                .id(memberId)
                .nickname(memberModifyRequest.getNickname())
                .provider(ProviderType.byProviderName(provider))
                .phone(memberModifyRequest.getPhone())
                .build();

        when(this.memberRepository.save(any(Member.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        MemberResponse memberResponse = this.memberService.modifyMember(member, memberModifyRequest);

        // then
        assertThat(memberResponse.getId()).isEqualTo(memberId);
        assertThat(memberResponse.getNickname()).isEqualTo(modifyNickname);
        assertThat(memberResponse.getPhone()).isEqualTo(modifyPhone);
    }

    @Test
    @DisplayName("나의 문의내역 조회")
    public void getMyQuestionsTest() {
        // given
        when(this.questionRepository.findByMemberIdOrderByCreatedAtDesc(any(), any(Pageable.class))).thenReturn(this.getQuestions());

        // when
        Page<MemberQuestionListResponse> questions = this.memberService.getMyQuestions(memberId, page, size);

        // then
        List<MemberQuestionListResponse> content = questions.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(questionId);
        assertThat(content.get(0).getTitle()).isEqualTo(title);
        assertThat(content.get(0).getQuestionStatus()).isEqualTo(questionStatus);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
    }

    @Test
    @DisplayName("나의 주문내역 조회")
    public void getMyOrdersTest() {
        // given
        when(this.orderRepository.findByMemberIdOrderByCreatedAtDesc(any(), any(Pageable.class))).thenReturn(this.getOrders());

        // when
        Page<OrderResponse> orders = this.memberService.getMyOrders(memberId, page, size);


        // then
        List<OrderResponse> content = orders.getContent();
        List<OrderDetailResponse> orderDetails = content.get(0).getOrderDetails();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(orderId);
        assertThat(content.get(0).getOrderNo()).isEqualTo(orderNo);
        assertThat(content.get(0).getOrderStatus()).isEqualTo(orderStatus);
        assertThat(content.get(0).getOrderPrice()).isEqualTo(orderPrice);
        assertThat(content.get(0).getRecipient()).isEqualTo(recipient);
        assertThat(content.get(0).getPhone()).isEqualTo(recipientPhone);
        assertThat(content.get(0).getPostcode()).isEqualTo(postcode);
        assertThat(content.get(0).getAddress()).isEqualTo(address);
        assertThat(content.get(0).getAddressDetail()).isEqualTo(addressDetail);
        assertThat(content.get(0).getDescription()).isEqualTo(description);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);

        assertThat(orderDetails).isInstanceOf(List.class);
        assertThat(orderDetails.size()).isEqualTo(1);
        assertThat(orderDetails.get(0).getId()).isEqualTo(orderDetailId);
        assertThat(orderDetails.get(0).getQuantity()).isEqualTo(quantity);
        assertThat(orderDetails.get(0).getTotalPrice()).isEqualTo(totalPrice);
    }

    @Test
    @DisplayName("나의 배송지 목록 조회")
    public void getMyAddressesTest() {
        // given
        when(this.addressRepository.findAllByMemberIdOrderByIsPrimaryDescCreatedAtDesc(any())).thenReturn(List.of(this.getAddressData()));

        // when
        List<AddressResponse> addressResponses = this.memberService.getMyAddresses(memberId);

        // then
        assertThat(addressResponses).isInstanceOf(List.class);
        assertThat(addressResponses.size()).isEqualTo(1);
        assertThat(addressResponses.get(0).getId()).isEqualTo(addressId);
        assertThat(addressResponses.get(0).getIsPrimary()).isEqualTo(isPrimary);
        assertThat(addressResponses.get(0).getAddress()).isEqualTo(address);
        assertThat(addressResponses.get(0).getAddressDetail()).isEqualTo(addressDetail);
        assertThat(addressResponses.get(0).getPostcode()).isEqualTo(postcode);
    }

    @Test
    @DisplayName("나의 작성할 리뷰 목록 조회")
    public void getMyPendingReviews() {
        // given
        when(this.orderDetailRepository.findByOrderMemberIdAndReviewIsNull(any(), any(Pageable.class))).thenReturn(this.getOrderDetails());

        // when
        Page<MemberReviewResponse<?>> pendingReviews = this.memberService.getMyReviews(memberId, ReviewStatus.of(pendingStatus), page, size);

        // then
        List<MemberReviewResponse<?>> content = pendingReviews.getContent();
        OrderDetailResponse response = (OrderDetailResponse) content.get(0).getResponse();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getStatus()).isEqualTo(pendingStatus);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(reviewId);
        assertThat(response.getName()).isEqualTo(itemName);
        assertThat(response.getQuantity()).isEqualTo(quantity);
        assertThat(response.getTotalPrice()).isEqualTo(totalPrice);
    }

    @Test
    @DisplayName("나의 작성한 리뷰 목록 조회")
    public void getMyCompleteReviews() {
        // given
        when(this.reviewRepository.findAllByMemberIdAndDeletedAtIsNull(any(), any(Pageable.class))).thenReturn(this.getReviews());

        // when
        Page<MemberReviewResponse<?>> completeReviews = this.memberService.getMyReviews(memberId, ReviewStatus.of(completeStatus), page, size);

        // then
        List<MemberReviewResponse<?>> content = completeReviews.getContent();
        ReviewResponse response = (ReviewResponse) content.get(0).getResponse();
        OrderDetailResponse productInfo = response.getProductInfo();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getStatus()).isEqualTo(completeStatus);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(reviewId);
        assertThat(response.getScore()).isEqualTo(score);
        assertThat(response.getContent()).isEqualTo(reviewContent);

        assertThat(productInfo).isNotNull();
        assertThat(productInfo.getId()).isEqualTo(reviewId);
        assertThat(productInfo.getName()).isEqualTo(itemName);
        assertThat(productInfo.getQuantity()).isEqualTo(quantity);
        assertThat(productInfo.getTotalPrice()).isEqualTo(totalPrice);
    }

    private Page<Question> getQuestions() {
        List<Question> list = List.of(this.getQuestionData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(list, pageable, list.size());
    }

    private Page<Order> getOrders() {
        Order order = this.getOrderData();
        order.getOrderDetails().add(this.getOrderDetailData());

        List<Order> list = List.of(order);
        Pageable pageable = PageRequest.of(page, size);

        return new PageImpl<>(list, pageable, list.size());
    }

    private Page<OrderDetail> getOrderDetails() {
        List<OrderDetail> list = List.of(this.getOrderDetailData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(list, pageable, list.size());
    }

    private Page<Review> getReviews() {
        List<Review> list = List.of(this.getReviewData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(list, pageable, list.size());
    }

    private Member getMemberData() {
        return Member.builder()
                .id(memberId)
                .email(email)
                .provider(ProviderType.byProviderName(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.byRole(role))
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    private Question getQuestionData() {
        return Question.builder()
                .id(questionId)
                .member(this.getMemberData())
                .title(title)
                .content(content)
                .status(QuestionStatus.ofStatus(questionStatus))
                .createdAt(createdAt)
                .build();
    }

    private Item getItemData() {
        return Item.builder()
                .name(itemName)
                .price(itemPrice)
                .info(itemInfo)
                .build();
    }

    private Order getOrderData() {
        Order order =
                Order.builder()
                        .id(orderId)
                        .orderNo(orderNo)
                        .orderStatus(OrderStatus.valueOf(orderStatus))
                        .price(orderPrice)
                        .recipient(recipient)
                        .phone(recipientPhone)
                        .address(address)
                        .detail(addressDetail)
                        .description(description)
                        .postcode(postcode)
                        .createdAt(createdAt)
                        .build();
        order.addMember(this.getMemberData());

        return order;
    }

    private OrderDetail getOrderDetailData() {
        OrderDetail orderDetail =
                OrderDetail.builder()
                        .id(orderDetailId)
                        .itemPrice(itemPrice)
                        .quantity(quantity)
                        .totalPrice(totalPrice)
                        .build();
        orderDetail.addItem(this.getItemData());
        orderDetail.addOrder(this.getOrderData());

        return orderDetail;
    }

    private Address getAddressData() {
        return Address.builder()
                .id(addressId)
                .member(this.getMemberData())
                .isPrimary(isPrimary)
                .address(address)
                .addressDetail(addressDetail)
                .postcode(postcode)
                .build();
    }

    private Review getReviewData() {
        return Review.builder()
                .id(reviewId)
                .score(score)
                .content(reviewContent)
                .item(this.getItemData())
                .orderDetail(this.getOrderDetailData())
                .member(this.getMemberData())
                .build();
    }
}
