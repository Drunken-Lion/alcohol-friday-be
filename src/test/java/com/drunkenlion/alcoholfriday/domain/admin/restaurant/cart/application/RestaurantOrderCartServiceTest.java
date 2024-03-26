package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.request.RestaurantOrderCartUpdateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderCartListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderCartSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Test
    @DisplayName("Owner는 장바구니 수정이 가능하다.")
    public void t4() {
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

        RestaurantOrderCartUpdateRequest request = RestaurantOrderCartUpdateRequest.builder()
                .quantity(50L)
                .build();

        Mockito.when(restaurantOrderCartDetailRepository.findByIdAndDeletedAtIsNull(restaurantOrderCartDetail.getId())).thenReturn(Optional.of(restaurantOrderCartDetail));

        RestaurantOrderCartSaveResponse response = restaurantOrderCartService.updateRestaurantOrderCart(restaurantOrderCartDetail.getId(), request, member);

        assertThat(response.getName()).isEqualTo(product.getName());
        assertThat(response.getMakerName()).isEqualTo(maker.getName());
        assertThat(response.getPrice()).isEqualTo(product.getDistributionPrice());
        assertThat(response.getQuantity()).isEqualTo(restaurantOrderCartDetail.getQuantity());

        assertThat(response.getId()).isEqualTo(product.getId());
        assertThat(response.getQuantity()).isEqualTo(request.getQuantity());
    }

    @Test
    @DisplayName("Owner가 아니면 수량 수정이 불가하다.")
    public void t5() {
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
                .role(MemberRole.MEMBER)
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

        RestaurantOrderCartUpdateRequest request = RestaurantOrderCartUpdateRequest.builder()
                .quantity(50L)
                .build();

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            restaurantOrderCartService.updateRestaurantOrderCart(restaurantOrderCartDetail.getId(), request, member);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.FORBIDDEN.getStatus());
    }

    @Test
    @DisplayName("입력한 수량이 0 이하면 수정이 불가하다.")
    public void t6() {
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

        RestaurantOrderCartUpdateRequest request = RestaurantOrderCartUpdateRequest.builder()
                .quantity(-2L)
                .build();

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            restaurantOrderCartService.updateRestaurantOrderCart(restaurantOrderCartDetail.getId(), request, member);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.NOT_FOUND_RESTAURANT_ORDER_CART_DETAIL.getStatus());
    }

    @Test
    @DisplayName("입력한 수량이 재고 수량 이상이면 수정이 불가하다.")
    public void t7() {
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

        RestaurantOrderCartUpdateRequest request = RestaurantOrderCartUpdateRequest.builder()
                .quantity(1000L)
                .build();

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            restaurantOrderCartService.updateRestaurantOrderCart(restaurantOrderCartDetail.getId(), request, member);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.NOT_FOUND_RESTAURANT_ORDER_CART_DETAIL.getStatus());
    }

    @Test
    @DisplayName("Owner는 장바구니 제품 삭제가 가능하다.")
    public void t8() {
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

        Mockito.when(restaurantOrderCartDetailRepository.findByIdAndDeletedAtIsNull(restaurantOrderCartDetail.getId())).thenReturn(Optional.of(restaurantOrderCartDetail));
        restaurantOrderCartService.deleteRestaurantOrderCart(restaurantOrderCartDetail.getId(), member);
    }

    @Test
    @DisplayName("Owner가 아니면 제품 삭제가 불가하다.")
    public void t9() {
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
                .role(MemberRole.MEMBER)
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

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            restaurantOrderCartService.deleteRestaurantOrderCart(restaurantOrderCartDetail.getId(), member);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.FORBIDDEN.getStatus());
    }

    @Test
    @DisplayName("Owner는 장바구니 목록 조회")
    public void t10() {
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

        Product product2 = Product.builder()
                .id(2L)
                .name("테스트 제품2")
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
                .quantity(10L)
                .restaurantOrderCart(restaurantOrderCart)
                .build();

        RestaurantOrderCartDetail restaurantOrderCartDetail2 = RestaurantOrderCartDetail.builder()
                .id(2L)
                .product(product2)
                .quantity(10L)
                .restaurantOrderCart(restaurantOrderCart)
                .build();

        when(this.restaurantRepository.findByIdAndDeletedAtIsNull(restaurant.getId())).thenReturn(Optional.of(restaurant));
        when(this.restaurantOrderCartRepository.findRestaurantAndMember(restaurant, member)).thenReturn(Optional.of(restaurantOrderCart));
        when(this.restaurantOrderCartDetailRepository.findRestaurantAndMember(any(), any(), any()))
                .thenReturn(getRestaurantsOrderCartDetails(RestaurantOrderCartDetail.builder()
                        .id(1L)
                        .product(product)
                        .quantity(10L)
                        .restaurantOrderCart(restaurantOrderCart)
                        .build()
                ));

        Page<RestaurantOrderCartListResponse> restaurantOrderCartList = this.restaurantOrderCartService.findRestaurantCart(restaurant.getId(), member, 0, 20);

        List<RestaurantOrderCartListResponse> content = restaurantOrderCartList.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.get(0).getId()).isEqualTo(product.getId());
        assertThat(content.get(0).getMakerName()).isEqualTo(maker.getName());
        assertThat(content.get(0).getPrice()).isEqualTo(product.getDistributionPrice());
        assertThat(content.get(0).getQuantity()).isEqualTo(restaurantOrderCartDetail.getQuantity());
    }

    @Test
    @DisplayName("Owner가 아니면 장바구니 목록 조회가 불가하다.")
    public void t11() {
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
                .role(MemberRole.MEMBER)
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

        BusinessException businessException = assertThrows(BusinessException.class, () -> {
            restaurantOrderCartService.findRestaurantCart(restaurant.getId(), member, 0, 20);
        });

        assertThat(businessException.getStatus()).isEqualTo(Fail.FORBIDDEN.getStatus());
    }

    private Page<RestaurantOrderCartDetail> getRestaurantsOrderCartDetails(RestaurantOrderCartDetail restaurantOrderCart) {
        List<RestaurantOrderCartDetail> list = List.of(restaurantOrderCart);
        Pageable pageable = PageRequest.of(0, 20);
        return new PageImpl<RestaurantOrderCartDetail>(list, pageable, list.size());
    }
}