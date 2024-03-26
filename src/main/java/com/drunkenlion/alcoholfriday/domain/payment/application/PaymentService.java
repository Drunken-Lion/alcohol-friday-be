package com.drunkenlion.alcoholfriday.domain.payment.application;

import com.drunkenlion.alcoholfriday.domain.payment.dto.request.TossPaymentsReq;

import java.math.BigDecimal;

public interface PaymentService {
    void validatePaymentAmount(String orderNo, BigDecimal amount);
    void saveSuccessPayment(TossPaymentsReq tossPaymentsReq);
    void deletedCartItems(String orderNo);
}
