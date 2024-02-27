package com.drunkenlion.alcoholfriday.domain.member.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.customerservice.dao.QuestionRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.entity.Question;
import com.drunkenlion.alcoholfriday.domain.customerservice.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.dto.*;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
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
    private final String address = "서울시 마포구 연남동";
    private final String addressDetail = "123-12";
    private final String description = "부재시 연락주세요.";
    private final Long postcode = 123123L;

    private final Long orderDetailId = 1L;

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
                .provider(ProviderType.ofProvider(provider))
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
        Page<MemberOrderListResponse> orders = this.memberService.getMyOrders(memberId, page, size);


        // then
        List<MemberOrderListResponse> content = orders.getContent();
        List<MemberOrderDetailResponse> orderDetails = content.get(0).getOrderDetails();

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
        assertThat(orderDetails.get(0).getItemPrice()).isEqualTo(itemPrice);
        assertThat(orderDetails.get(0).getQuantity()).isEqualTo(quantity);
        assertThat(orderDetails.get(0).getTotalPrice()).isEqualTo(totalPrice);
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

    private Member getMemberData() {
        return Member.builder()
                .id(memberId)
                .email(email)
                .provider(ProviderType.ofProvider(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.ofRole(role))
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
        Member member = getMemberData();

        return Question.builder()
                .id(questionId)
                .member(member)
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
}
