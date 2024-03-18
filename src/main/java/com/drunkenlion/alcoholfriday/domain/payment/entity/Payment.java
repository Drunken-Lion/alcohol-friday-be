package com.drunkenlion.alcoholfriday.domain.payment.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentCardCode;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentCardType;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentMethod;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentOwnerType;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentProvider;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentStatus;
import com.drunkenlion.alcoholfriday.domain.payment.util.converter.*;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
public class Payment extends BaseEntity {
	@Comment("결제 번호")
	@Column(name = "payment_no", columnDefinition = "VARCHAR(200)")
	private String paymentNo;

	@Comment("결제 상태정보")
	@Column(name = "status", columnDefinition = "VARCHAR(20)")
	@Convert(converter = PaymentStatusConverter.class)
	private PaymentStatus paymentStatus;

	@Comment("결제 수단")
	@Column(name = "method", columnDefinition = "VARCHAR(20)")
	@Convert(converter = PaymentMethodConverter.class)
	private PaymentMethod paymentMethod;

	@Comment("easyPay_간편결제사 코드")
	@Column(name = "provider", columnDefinition = "VARCHAR(20)")
	@Convert(converter = PaymentProviderConverter.class)
	private PaymentProvider paymentProvider;

	@Comment("card_카드 종류")
	@Column(name = "card_type", columnDefinition = "VARCHAR(20)")
	@Convert(converter = PaymentCardTypeConverter.class)
	private PaymentCardType paymentCardType;

	@Comment("card_카드의 소유자 타입")
	@Column(name = "owner_type", columnDefinition = "VARCHAR(20)")
	@Convert(converter = PaymentOwnerTypeConverter.class)
	private PaymentOwnerType paymentOwnerType;

	@Comment("card_카드 발급사")
	@Column(name = "issuer_code", columnDefinition = "VARCHAR(20)")
	@Convert(converter = PaymentCardCodeConverter.class)
	private PaymentCardCode issuerCode;

	@Comment("card_카드 매입사")
	@Column(name = "acquirer_code", columnDefinition = "VARCHAR(20)")
	@Convert(converter = PaymentCardCodeConverter.class)
	private PaymentCardCode acquirerCode;

	@Comment("결제 총 금액")
	@Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
	private BigDecimal totalPrice;

	@Comment("결제 요청일")
	@Column(name = "requested_at", columnDefinition = "DATETIME")
	private LocalDateTime requestedAt;

	@Comment("결제 승인일")
	@Column(name = "approved_at", columnDefinition = "DATETIME")
	private LocalDateTime approvedAt;

	@Comment("결제 통화")
	@Column(name = "currency", columnDefinition = "VARCHAR(20)")
	private String currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;
}