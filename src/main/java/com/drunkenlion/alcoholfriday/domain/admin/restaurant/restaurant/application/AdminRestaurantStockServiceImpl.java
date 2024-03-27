package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantStockModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockModifyResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.util.RestaurantStockValidator;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.util.RestaurantValidator;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminRestaurantStockServiceImpl implements AdminRestaurantStockService {
    private final RestaurantRepository restaurantRepository;
    private final RestaurantStockRepository restaurantStockRepository;
    private final FileService fileService;

    @Override
    public Page<RestaurantStockListResponse> getRestaurantStocks(Member member, Long restaurantId, int page, int size) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(restaurantId)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_RESTAURANT));

        if (member.getRole().equals(MemberRole.OWNER)) {
            RestaurantValidator.validateOwnership(member, restaurant);
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<RestaurantStock> stockPages = restaurantStockRepository.findRestaurantStocks(member, restaurant, pageable);

        return stockPages.map(stock -> RestaurantStockListResponse.of(stock, fileService.findOne(stock.getProduct())));
    }

    @Transactional
    @Override
    public RestaurantStockModifyResponse modifyRestaurantStock(Long restaurantId,
                                                               Member member,
                                                               RestaurantStockModifyRequest modifyRequest) {

        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(restaurantId)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_RESTAURANT));

        RestaurantStockValidator.validateOwnership(member, restaurant);

        RestaurantStock restaurantStock =
                restaurantStockRepository.findByIdAndDeletedAtIsNull(modifyRequest.getId())
                        .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_RESTAURANT_STOCK));

        RestaurantStockValidator.validateStockInRestaurant(restaurantStock, restaurant);

        RestaurantStockValidator.validateOwnerModifyQuantity(member, restaurantStock, modifyRequest);

        RestaurantStockValidator.validateNegative(modifyRequest);

        restaurantStock = restaurantStockRepository.save(restaurantStock.toBuilder()
                .price(modifyRequest.getPrice())
                .quantity(modifyRequest.getQuantity())
                .build());

        return RestaurantStockModifyResponse.of(restaurantStock);
    }
}
