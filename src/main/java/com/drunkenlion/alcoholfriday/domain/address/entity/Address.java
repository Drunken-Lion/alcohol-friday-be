package com.drunkenlion.alcoholfriday.domain.address.entity;

import com.drunkenlion.alcoholfriday.domain.address.dto.AddressModifyRequest;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "address")
public class Address extends BaseEntity {
    @Comment("주소 소유자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Comment("대표 주소 여부")
    @Column(name = "is_primary", columnDefinition = "TINYINT")
    private Boolean isPrimary;

    @Comment("주소")
    @Column(name = "address", columnDefinition = "VARCHAR(200)")
    private String address;

    @Comment("상세 주소")
    @Column(name = "address_detail", columnDefinition = "VARCHAR(200)")
    private String addressDetail;

    @Comment("우편번호")
    @Column(name = "postcode", columnDefinition = "VARCHAR(50)")
    private String postcode;

    @Comment("받는 사람")
    @Column(name = "recipient", columnDefinition = "VARCHAR(50)")
    private String recipient;

    @Comment("받는 사람 연락처")
    @Column(name = "phone", columnDefinition = "BIGINT")
    private Long phone;

    @Comment("배송시 요청사항")
    @Column(name = "request", columnDefinition = "MEDIUMTEXT")
    private String request;

    public void changePrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }

    public void updateAddress(AddressModifyRequest modifyRequest) {
        this.recipient = modifyRequest.getRecipient();
        this.address = modifyRequest.getAddress();
        this.addressDetail = modifyRequest.getAddressDetail();
        this.postcode = modifyRequest.getPostcode();
        this.phone = modifyRequest.getPhone();
        this.request = modifyRequest.getRequest();
        this.isPrimary = modifyRequest.getIsPrimary();
    }
}
