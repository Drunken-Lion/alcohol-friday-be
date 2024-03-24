package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderCartSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("[RestaurantOrderCartServiceTest] RestaurantOrderCart Service Test")
class RestaurantOrderCartServiceTest {
    @InjectMocks
    private RestaurantOrderCartServiceImpl restaurantOrderCartService;

    @Mock
    private RestaurantOrderCartRepository restaurantOrderCartRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantOrderCartDetailRepository restaurantOrderCartDetailRepository;

    @Mock
    private ProductRepository productRepository;
    @Mock
    private FileService fileService;

    @AfterEach
    @Transactional
    public void after() {
        restaurantOrderCartRepository.deleteAll();
        restaurantOrderCartDetailRepository.deleteAll();
        restaurantRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("Owner는 장바구니 저장이 가능하다.")
    public void t1() {
        Maker maker = Maker.builder()
                .id(1L)
                .name("테스트")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build();

        Member member = Member.builder()
                .id(1L)
                .role(MemberRole.OWNER)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .member(member)
                .build();

        RestaurantOrderCart restaurantOrderCart = RestaurantOrderCart.builder()
                .id(1L)
                .member(member)
                .restaurant(restaurant)
                .build();

        RestaurantOrderCartDetail restaurantOrderCartDetail = RestaurantOrderCartDetail.builder()
                .id(1L)
                .product(product)
                .quantity(1L)
                .restaurantOrderCart(restaurantOrderCart)
                .build();

        RestaurantOrderCartSaveRequest request = RestaurantOrderCartSaveRequest.builder()
                .restaurantId(restaurant.getId())
                .productId(product.getId())
                .quantity(1L)
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(restaurant.getId())).thenReturn(
                Optional.of(restaurant)
        );

        when(restaurantOrderCartRepository.findRestaurantAndMember(restaurant, member)).thenReturn(
                Optional.of(restaurantOrderCart)
        );

        when(productRepository.findByIdAndDeletedAtIsNull(product.getId())).thenReturn(
                Optional.of(product)
        );

        when(restaurantOrderCartDetailRepository.findCartAndProduct(restaurantOrderCart, product)).thenReturn(
                Optional.of(restaurantOrderCartDetail)
        );

        RestaurantOrderCartSaveResponse response = restaurantOrderCartService.saveRestaurantOrderCart(request, member);

        assertThat(response.getName()).isEqualTo(product.getName());
        assertThat(response.getMakerName()).isEqualTo(maker.getName());
        assertThat(response.getPrice()).isEqualTo(product.getDistributionPrice());
        assertThat(response.getQuantity()).isEqualTo(restaurantOrderCartDetail.getQuantity());
    }

    @Test
    @DisplayName("Owner가 아니면 장바구니 저장이 불가능하다.")
    public void t2() {
        Member member = Member.builder()
                .id(1L)
                .role(MemberRole.MEMBER)
                .build();

        RestaurantOrderCartSaveRequest request = RestaurantOrderCartSaveRequest.builder()
                .restaurantId(1L)
                .productId(1L)
                .quantity(1L)
                .build();

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            restaurantOrderCartService.saveRestaurantOrderCart(request, member);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.FORBIDDEN.getStatus());
    }

    @Test
    @DisplayName("제품의 재고가 장바구니에 담을 제품의 수량보다 적으면 안된다.")
    public void t3() {
        Maker maker = Maker.builder()
                .id(1L)
                .name("테스트")
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("테스트 제품")
                .quantity(100L)
                .distributionPrice(BigDecimal.valueOf(10000L))
                .maker(maker)
                .build();

        Member member = Member.builder()
                .id(1L)
                .role(MemberRole.OWNER)
                .build();

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .member(member)
                .build();

        RestaurantOrderCart restaurantOrderCart = RestaurantOrderCart.builder()
                .id(1L)
                .member(member)
                .restaurant(restaurant)
                .build();

        RestaurantOrderCartSaveRequest request = RestaurantOrderCartSaveRequest.builder()
                .restaurantId(restaurant.getId())
                .productId(product.getId())
                .quantity(1000L)
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(restaurant.getId())).thenReturn(
                Optional.of(restaurant)
        );

        when(restaurantOrderCartRepository.findRestaurantAndMember(restaurant, member)).thenReturn(
                Optional.of(restaurantOrderCart)
        );

        when(productRepository.findByIdAndDeletedAtIsNull(product.getId())).thenReturn(
                Optional.of(product)
        );

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            restaurantOrderCartService.saveRestaurantOrderCart(request, member);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.OUT_OF_PRODUCT_STOCK.getStatus());
    }
}