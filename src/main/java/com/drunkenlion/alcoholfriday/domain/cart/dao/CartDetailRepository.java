package com.drunkenlion.alcoholfriday.domain.cart.dao;

import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    List<CartDetail> findAllByCart(Cart cart);
}