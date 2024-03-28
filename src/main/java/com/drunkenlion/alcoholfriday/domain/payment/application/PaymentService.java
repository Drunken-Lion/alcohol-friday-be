package com.drunkenlion.alcoholfriday.domain.payment.application;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.payment.dto.request.TossPaymentsReq;
import com.drunkenlion.alcoholfriday.domain.payment.dto.response.TossApiResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;

public interface PaymentService {
    void validatePaymentAmount(String orderNo, BigDecimal amount);
    void saveSuccessPayment(TossPaymentsReq tossPaymentsReq, Order order, Member member);
    void deletedCartItems(List<OrderDetail> orderDetails, Member member);
    void checkDeletedData(List<OrderDetail> orderDetails);
    void checkCancelPayment(Order order, List<OrderDetail> orderDetails, Member member);
    void saveCancelSuccessPayment(TossPaymentsReq tossPaymentsReq, Order order, List<OrderDetail> orderDetails, Member member);
    TossApiResponse getTossPaymentsResult(URL url, JSONObject obj, JSONParser parser) throws Exception ;
}
