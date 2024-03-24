package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveCodeRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantAdminOrderApprovalResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderSaveCodeResponse;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.dao.RestaurantOrderCartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.dao.RestaurantOrderCartRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.dao.RestaurantOrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
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
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
@DisplayName("[RestaurantOrderServiceImplV2Test] 레스토랑 발주 Service Test")
class RestaurantOrderServiceImplV2Test {
    @InjectMocks
    private RestaurantOrderServiceImplV2 orderService;

    @Mock
    private RestaurantOrderRepository restaurantOrderRepository;
    @Mock
    private RestaurantOrderDetailRepository restaurantOrderDetailRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private RestaurantOrderCartRepository restaurantOrderCartRepository;
    @Mock
    private RestaurantOrderCartDetailRepository restaurantOrderCartDetailRepository;
    @Mock
    private RestaurantStockRepository restaurantStockRepository;

    @Mock
    private FileService fileService;

    @AfterEach
    @Transactional
    public void after() {
        restaurantOrderRepository.deleteAll();
        restaurantOrderDetailRepository.deleteAll();
        productRepository.deleteAll();
        restaurantRepository.deleteAll();
        restaurantOrderCartRepository.deleteAll();
        restaurantOrderCartDetailRepository.deleteAll();
        restaurantStockRepository.deleteAll();
    }

    @Test
    @DisplayName("Owner는 발주를 할 수 있다.")
    public void t1() {
        Member member = Member.builder()
                .id(1L)
                .role(MemberRole.OWNER)
                .build();

        Maker maker = Maker.builder().id(1L).build();

        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .businessName("섭컴퍼니")
                .member(member)
                .build();

        Mockito.when(restaurantRepository.findById(1L)).thenReturn(
                Optional.of(restaurant)
        );

        Product product = Product.builder()
                .id(1L)
                .name("테스트 제품")
                .quantity(100L)
                .price(BigDecimal.valueOf(10000L))
                .distributionPrice(BigDecimal.valueOf(11000L))
                .maker(maker)
                .build();

        RestaurantOrderCart restaurantOrderCart = RestaurantOrderCart.builder()
                .id(1L)
                .restaurant(restaurant)
                .member(member)
                .build();

        RestaurantOrderCartDetail restaurantOrderCartDetail = RestaurantOrderCartDetail.builder()
                .id(1L)
                .product(product)
                .quantity(2L)
                .restaurantOrderCart(restaurantOrderCart)
                .build();
        restaurantOrderCartDetail.addCart(restaurantOrderCart);

        RestaurantOrderCartDetail restaurantOrderCartDetail2 = RestaurantOrderCartDetail.builder()
                .id(2L)
                .product(product)
                .quantity(2L)
                .restaurantOrderCart(restaurantOrderCart)
                .build();
        restaurantOrderCartDetail2.addCart(restaurantOrderCart);

        List<RestaurantOrderCartDetail> cartDetails = List.of(restaurantOrderCartDetail, restaurantOrderCartDetail2);

        Mockito.when(restaurantOrderCartDetailRepository.findRestaurantAndMember(restaurant, member)).thenReturn(
                cartDetails
        );

        RestaurantOrderSaveCodeRequest request = RestaurantOrderSaveCodeRequest.builder()
                .restaurantId(restaurant.getId())
                .build();

        RestaurantOrderSaveCodeResponse response = orderService.getSaveCode(request, member);

        assertThat(response.getMember().getId()).isEqualTo(member.getId());
        assertThat(response.getBusinessName()).isEqualTo(restaurant.getBusinessName());
        assertThat(response.getTotalPrice()).isNotNull();
        assertThat(response.getDetails().size()).isEqualTo(2);
        assertThat(response.getStatus()).isEqualTo(RestaurantOrderStatus.ADD_INFO);
    }

    @Test
    @DisplayName("Admin은 발주 승인이 가능하다.")
    public void t2() {
        Member member = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .businessName("섭컴퍼니")
                .member(member)
                .build();

        RestaurantOrder restaurantOrder = RestaurantOrder.builder().id(1L)
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .restaurant(restaurant)
                .build();

        Mockito.when(restaurantOrderRepository.findRestaurantOrderAdmin(restaurantOrder.getId())).thenReturn(
                Optional.of(restaurantOrder)
        );

        RestaurantAdminOrderApprovalResponse response = orderService.adminOrderApproval(
                restaurantOrder.getId(), member);

        assertThat(response.getBusinessName()).isEqualTo(restaurant.getBusinessName());
        assertThat(response.getStatus()).isEqualTo(RestaurantOrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("Admin은 발주 반려가 가능하다.")
    public void t3() {
        Member member = Member.builder().id(1L).role(MemberRole.ADMIN).build();
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .businessName("섭컴퍼니")
                .member(member)
                .build();

        RestaurantOrder restaurantOrder = RestaurantOrder.builder().id(1L)
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .restaurant(restaurant)
                .build();

        Mockito.when(restaurantOrderRepository.findRestaurantOrderAdmin(restaurantOrder.getId())).thenReturn(
                Optional.of(restaurantOrder)
        );

        RestaurantAdminOrderApprovalResponse response = orderService.adminOrderRejectedApproval(
                restaurantOrder.getId(), member);

        assertThat(response.getBusinessName()).isEqualTo(restaurant.getBusinessName());
        assertThat(response.getStatus()).isEqualTo(RestaurantOrderStatus.REJECTED_APPROVAL);
    }

    @Test
    @DisplayName("Admin이 아니면 발주 승인이 불가하다.")
    public void t4() {
        Member member = Member.builder().id(1L).role(MemberRole.MEMBER).build();
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .businessName("섭컴퍼니")
                .member(member)
                .build();

        RestaurantOrder restaurantOrder = RestaurantOrder.builder().id(1L)
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .restaurant(restaurant)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.adminOrderApproval(restaurantOrder.getId(), member);
        });

        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("Admin이 아니면 발주 반려 불가하다.")
    public void t5() {
        Member member = Member.builder().id(1L).role(MemberRole.MEMBER).build();
        Restaurant restaurant = Restaurant.builder()
                .id(1L)
                .businessName("섭컴퍼니")
                .member(member)
                .build();

        RestaurantOrder restaurantOrder = RestaurantOrder.builder().id(1L)
                .orderStatus(RestaurantOrderStatus.WAITING_APPROVAL)
                .restaurant(restaurant)
                .build();

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            orderService.adminOrderRejectedApproval(restaurantOrder.getId(), member);
        });

        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }
}