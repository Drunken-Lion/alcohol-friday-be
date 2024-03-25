package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantOrderCartDetailRepository extends JpaRepository<RestaurantOrderCartDetail, Long>, RestaurantOrderCartDetailCustomRepository {
    Optional<RestaurantOrderCartDetail> findByIdAndDeletedAtIsNull(Long id);
}
