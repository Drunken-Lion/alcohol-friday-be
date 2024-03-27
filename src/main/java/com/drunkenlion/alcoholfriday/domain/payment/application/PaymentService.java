package com.drunkenlion.alcoholfriday.domain.payment.application;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderCancelCompleteRequest;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.payment.dto.request.TossPaymentsReq;
import com.drunkenlion.alcoholfriday.domain.payment.dto.response.TossApiResponse;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

public interface PaymentService {
    void validatePaymentAmount(String orderNo, BigDecimal amount);
    void saveSuccessPayment(TossPaymentsReq tossPaymentsReq);
    void deletedCartItems(String orderNo);
    void checkCancelPayment(OrderCancelCompleteRequest orderCancelCompleteRequest, Order order, List<OrderDetail> orderDetails, Member member);
    void saveCancelSuccessPayment(TossPaymentsReq tossPaymentsReq, Order order, List<OrderDetail> orderDetails, Member member);
    TossApiResponse getTossPaymentsResult(URL url, JSONObject obj) throws Exception ;
}
