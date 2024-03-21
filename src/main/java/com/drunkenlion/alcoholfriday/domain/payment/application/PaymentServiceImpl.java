package com.drunkenlion.alcoholfriday.domain.payment.application;

import com.drunkenlion.alcoholfriday.domain.cart.application.CartService;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.DeleteCartRequest;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.payment.dao.PaymentRepository;
import com.drunkenlion.alcoholfriday.domain.payment.dto.request.TossPaymentsReq;
import com.drunkenlion.alcoholfriday.domain.payment.entity.Payment;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentServiceImpl implements PaymentService {
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final PaymentRepository paymentRepository;

    // TODO 이름 바꾸기 - Check, Validity  따로 쓰기
    // 결제 금액 유효성 검증
    @Override
    public void checkAmountValidity(String orderNo, BigDecimal amount) {
        Order order = orderRepository.findByOrderNo(orderNo).orElseThrow(() -> BusinessException.builder()
                .response(HttpResponse.Fail.NOT_FOUND_ORDER)
                .build());

        if (!order.getOrderStatus().equals(OrderStatus.ORDER_RECEIVED))
            throw new BusinessException(HttpResponse.Fail.ORDER_ISSUE);

        if (!amount.equals(order.getTotalPrice()))
            throw new BusinessException(HttpResponse.Fail.BAD_REQUEST_AMOUNT);
    }

    // 결제 성공 시 Payment 저장
    @Override
    @Transactional
    public void saveSuccessPayment(TossPaymentsReq tossPaymentsReq) {
        Order order = orderRepository.findByOrderNo(tossPaymentsReq.getOrderNo())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ORDER)
                        .build());

        Member member = memberRepository.findByEmail(order.getMember().getEmail())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_MEMBER)
                        .build());

        Payment payment = TossPaymentsReq.toEntity(tossPaymentsReq, member, order);
        paymentRepository.save(payment);
    }

    // 결제 성공 시 장바구니에서 해당 주문 아이템 제거
    @Override
    @Transactional
    public void deletedCartItems(String orderNo) {
        Order order = orderRepository.findByOrderNo(orderNo)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ORDER)
                        .build());

        List<DeleteCartRequest> deleteCartRequests = order.getOrderDetails().stream()
                .map(orderDetail -> DeleteCartRequest.of(orderDetail.getItem().getId()))
                .toList();

        cartService.deleteCartList(deleteCartRequests, order.getMember());
    }
}
