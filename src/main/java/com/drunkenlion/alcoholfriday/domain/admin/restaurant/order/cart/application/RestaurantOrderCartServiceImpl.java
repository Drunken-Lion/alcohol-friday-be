package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request.RestaurantOrderCartDeleteRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request.RestaurantOrderCartSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.request.RestaurantOrderCartUpdateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.response.RestaurantOrderCartSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.cart.dto.response.RestaurantOrderProductListResponse;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.dao.RestaurantOrderCartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.dao.RestaurantOrderCartRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RestaurantOrderCartServiceImpl implements RestaurantOrderCartService {
    private final ProductRepository productRepository;
    private final FileService fileService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantOrderCartRepository restaurantOrderCartRepository;
    private final RestaurantOrderCartDetailRepository restaurantOrderCartDetailRepository;

    /**
     * 발주를 위한 제품 목록
     */
    @Override
    public Page<RestaurantOrderProductListResponse> getRestaurantOrderProducts(int page, int size, Member member) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);

        return products.map(product -> RestaurantOrderProductListResponse.of(product, fileService.findOne(product)));
    }

    /**
     * 장바구니 제품 추가
     */
    @Override
    @Transactional
    public RestaurantOrderCartSaveResponse saveRestaurantOrderCart(RestaurantOrderCartSaveRequest request,
                                                                   Member member) {
        Restaurant restaurant = restaurantRepository.findByIdAndDeletedAtIsNull(request.getRestaurantId())
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_RESTAURANT));

        RestaurantOrderCart restaurantOrderCart = restaurantOrderCartRepository.findRestaurantAndMember(restaurant, member)
                .orElseGet(() -> RestaurantOrderCart.builder()
                                .member(member)
                                .restaurant(restaurant)
                                .build());
        return null;
    }
}
