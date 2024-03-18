package com.drunkenlion.alcoholfriday.domain.restaurant.order.application;

import com.drunkenlion.alcoholfriday.domain.restaurant.order.dao.RestaurantOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RestaurantOrderServiceImpl implements RestaurantOrderService {
    private final RestaurantOrderRepository restaurantOrderRepository;
}
