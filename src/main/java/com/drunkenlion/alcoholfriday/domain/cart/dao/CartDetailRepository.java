package com.drunkenlion.alcoholfriday.domain.cart.dao;

import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    CartDetail findByItemAndCart(Item item, Cart cart);
}
