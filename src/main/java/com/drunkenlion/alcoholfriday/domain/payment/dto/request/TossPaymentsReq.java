package com.drunkenlion.alcoholfriday.domain.payment.dto.request;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.payment.entity.Payment;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "최종 결제 후 토스페이먼츠에서 주는 결제 응답 값")
public class TossPaymentsReq {
    @Schema(description = "주문 번호")
    private String orderNo;
    @Schema(description = "결제 번호")
    private String paymentNo;
    @Schema(description = "결제 상태정보")
    private String status;
    @Schema(description = "결제 수단")
    private String method;
    @Schema(description = "카드 종류")
    private String cardType;
    @Schema(description = "카드의 소유자 타입")
    private String ownerType;
    @Schema(description = "간편결제사 코드")
    private String provider;
    @Schema(description = "카드 발급사")
    private String issuerCode;
    @Schema(description = "카드 매입사")
    private String acquirerCode;
    @Schema(description = "결제 총 금액")
    private String totalAmount;
    @Schema(description = "결제 요청일")
    private String requestedAt;
    @Schema(description = "결제 승인일")
    private String approvedAt;
    @Schema(description = "결제 통화")
    private String currency;

    public static TossPaymentsReq of(String orderNo, String paymentKey, JSONObject jsonObject) {
        String status = (String) jsonObject.get("status");
        String method = (String) jsonObject.get("method");
        String totalAmount = (String) jsonObject.get("totalAmount");
        String requestedAt = (String) jsonObject.get("requestedAt");
        String approvedAt = (String) jsonObject.get("approvedAt");
        String currency = (String) jsonObject.get("currency");

        JSONObject cardObject = jsonObject.get("card") == null ? null : (JSONObject) jsonObject.get("card");
        String cardType = cardObject == null ? null : (String) cardObject.get("cardType");
        String ownerType = cardObject == null ? null : (String) cardObject.get("ownerType");
        String issuerCode = cardObject == null ? null : (String) cardObject.get("issuerCode");
        String acquirerCode = cardObject == null ? null : (String) cardObject.get("acquirerCode");

        JSONObject easyPayObject = jsonObject.get("easyPay") == null ? null : (JSONObject) jsonObject.get("easyPay");
        String provider = easyPayObject == null ? null : (String) easyPayObject.get("provider");

        return TossPaymentsReq.builder()
                .orderNo(orderNo)
                .paymentNo(paymentKey)
                .status(status)
                .method(method)
                .cardType(cardType)
                .ownerType(ownerType)
                .provider(provider)
                .issuerCode(issuerCode)
                .acquirerCode(acquirerCode)
                .totalAmount(totalAmount)
                .requestedAt(requestedAt)
                .approvedAt(approvedAt)
                .currency(currency)
                .build();
    }

    public static Payment toEntity(TossPaymentsReq tossPaymentsReq, Member member, Order order) {
        BigDecimal totalPrice = new BigDecimal(tossPaymentsReq.getTotalAmount());
        String requestedAtStr = tossPaymentsReq.getRequestedAt().substring(0, 19);
        String approvedAtStr = tossPaymentsReq.getApprovedAt().substring(0, 19);
        LocalDateTime requestedAt = LocalDateTime.parse(requestedAtStr);
        LocalDateTime approvedAt = LocalDateTime.parse(approvedAtStr);

        return Payment.builder()
                .paymentNo(tossPaymentsReq.getPaymentNo())
                .paymentStatus(PaymentStatus.ofStatus(tossPaymentsReq.getStatus()))
                .paymentMethod(PaymentMethod.ofMethod(tossPaymentsReq.getMethod()))
                .paymentProvider(PaymentProvider.ofPaymentProvider(tossPaymentsReq.getProvider()))
                .paymentCardType(PaymentCardType.ofCardType(tossPaymentsReq.getCardType()))
                .paymentOwnerType(PaymentOwnerType.ofOwnerType(tossPaymentsReq.getOwnerType()))
                .issuerCode(PaymentCardCode.ofCardCode(tossPaymentsReq.getIssuerCode()))
                .acquirerCode(PaymentCardCode.ofCardCode(tossPaymentsReq.getAcquirerCode()))
                .totalPrice(totalPrice)
                .requestedAt(requestedAt)
                .approvedAt(approvedAt)
                .currency(tossPaymentsReq.getCurrency())
                .order(order)
                .member(member)
                .build();
    }
}
