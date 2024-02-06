package com.drunkenlion.alcoholfriday.domain.cart.dao;

import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
}
