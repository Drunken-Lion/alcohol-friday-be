package com.drunkenlion.alcoholfriday.domain.cart.dao;

import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    Optional<CartDetail> findByItemAndCart(Item item, Cart cart);
    List<CartDetail> findAllByCart(Cart cart);
    List<CartDetail> findByItemAndDeletedAtIsNull(Item item);
    void deleteByItemAndCart(Item item, Cart cart);
}
