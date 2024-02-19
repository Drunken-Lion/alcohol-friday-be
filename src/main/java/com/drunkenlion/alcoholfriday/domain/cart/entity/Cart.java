package com.drunkenlion.alcoholfriday.domain.cart.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {
    @Comment("장바구니 주인")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Builder.Default
    @Comment("장바구니에 담긴 상품(들)")
    @OneToMany(mappedBy = "cart")
    private List<CartDetail> cartDetails = new ArrayList<>();
}
