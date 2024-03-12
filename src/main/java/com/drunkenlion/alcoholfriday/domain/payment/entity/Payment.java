package com.drunkenlion.alcoholfriday.domain.payment.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentCardCode;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentCardType;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentOwnerType;
import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentProvider;
import com.drunkenlion.alcoholfriday.domain.payment.util.converter.*;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.PaymentMethod;
import com.drunkenlion.alcoholfriday.global.common.enumerated.PaymentStatus;
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
	@Column(name = "payment_no", columnDefinition = "VARCHAR(200)")
	@Comment("결제 번호")
	private String paymentNo;

	@Column(name = "status", columnDefinition = "VARCHAR(20)")
	@Comment("결제 상태정보")
	@Convert(converter = PaymentStatusConverter.class)
	private PaymentStatus paymentStatus;

	@Column(name = "method", columnDefinition = "VARCHAR(20)")
	@Comment("결제 수단")
	@Convert(converter = PaymentMethodConverter.class)
	private PaymentMethod paymentMethod;

	@Column(name = "provider", columnDefinition = "VARCHAR(20)")
	@Comment("easyPay_간편결제사 코드")
	@Convert(converter = PaymentProviderConverter.class)
	private PaymentProvider paymentProvider;

	@Column(name = "card_type", columnDefinition = "VARCHAR(20)")
	@Comment("card_카드 종류")
	@Convert(converter = PaymentCardTypeConverter.class)
	private PaymentCardType paymentCardType;

	@Column(name = "owner_type", columnDefinition = "VARCHAR(20)")
	@Comment("card_카드의 소유자 타입")
	@Convert(converter = PaymentOwnerTypeConverter.class)
	private PaymentOwnerType paymentOwnerType;

	@Column(name = "issuer_code", columnDefinition = "VARCHAR(20)")
	@Comment("card_카드 발급사")
	@Convert(converter = PaymentCardCodeConverter.class)
	private PaymentCardCode issuerCode;

	@Column(name = "acquirer_code", columnDefinition = "VARCHAR(20)")
	@Comment("card_카드 매입사")
	@Convert(converter = PaymentCardCodeConverter.class)
	private PaymentCardCode acquirerCode;

	@Column(name = "total_price", columnDefinition = "DECIMAL(64, 3)")
	@Comment("결제 총 금액")
	private BigDecimal totalPrice;

	@Column(name = "requested_at", columnDefinition = "DATETIME")
	@Comment("결제 요청일")
	private LocalDateTime requestedAt;

	@Column(name = "approved_at", columnDefinition = "DATETIME")
	@Comment("결제 승인일")
	private LocalDateTime approvedAt;

	@Column(name = "currency", columnDefinition = "VARCHAR(20)")
	@Comment("결제 통화")
	private String currency;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Order order;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private Member member;
}