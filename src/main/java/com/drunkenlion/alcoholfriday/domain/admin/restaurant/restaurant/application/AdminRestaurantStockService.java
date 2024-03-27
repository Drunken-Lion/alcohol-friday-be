package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantStockModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockModifyResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import org.springframework.data.domain.Page;

public interface AdminRestaurantStockService {
    Page<RestaurantStockListResponse> getRestaurantStocks(Member member, Long restaurantId, int page, int size);

    RestaurantStockModifyResponse modifyRestaurantStock(Long restaurantId, Member member, RestaurantStockModifyRequest modifyRequest);
}
