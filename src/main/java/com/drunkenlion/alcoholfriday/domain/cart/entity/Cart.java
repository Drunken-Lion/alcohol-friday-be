package com.drunkenlion.alcoholfriday.domain.cart.entity;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart extends BaseEntity {
    @Comment("장바구니 주인")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    @Comment("장바구니에 담긴 상품")
    @OneToMany(mappedBy = "cart")
    private List<CartDetail> cartDetails = new ArrayList<>();
}
