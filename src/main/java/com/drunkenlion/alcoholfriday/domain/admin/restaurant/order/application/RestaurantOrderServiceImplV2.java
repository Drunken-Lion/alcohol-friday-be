package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application;


import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dao.RestaurantOrderCartRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCart;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.entity.RestaurantOrderCartDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveCodeRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.request.RestaurantOrderSaveRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderResultResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderSaveCodeResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderSaveResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.util.RestaurantValidator;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.common.util.RoleValidator;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RestaurantOrderServiceImplV2 {
    //TODO
    // - 레스토랑 관리자 기능 전체 구현 후 패키지 구조 및 Controller, Service 병합 예정

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantOrderDetailRepository restaurantOrderDetailRepository;
    private final ProductRepository productRepository;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantOrderCartRepository restaurantOrderCartRepository;
    private final RestaurantOrderCartDetailRepository restaurantOrderCartDetailRepository;
    private final RestaurantStockRepository restaurantStockRepository;
    private final FileService fileService;

    /**
     * 발주 정보 임시 저장 ID 값을 return 하고 장바구니 내 product 수량 차감 (Owner)
     */
    @Transactional
    public RestaurantOrderSaveCodeResponse getSaveCode(RestaurantOrderSaveCodeRequest request,
                                                       Member member) {
        RoleValidator.validateRole(member, MemberRole.OWNER);

        Restaurant restaurant =
                restaurantRepository.findById(request.getRestaurantId())
                        .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT));

        RestaurantOrder restaurantOrder = RestaurantOrder.builder()
                .member(member)
                .restaurant(restaurant)
                .orderStatus(RestaurantOrderStatus.ADD_INFO)
                .address(restaurant.getAddress())
                .addressDetail(restaurant.getAddressDetail())
                .postcode(restaurant.getPostcode())
                .build();
        restaurantOrderRepository.save(restaurantOrder);

        List<RestaurantOrderCartDetail> cartDetails = restaurantOrderCartDetailRepository.findRestaurantAndMember(
                restaurant, member);

        if (cartDetails.isEmpty()) {
            throw new BusinessException(Fail.NOT_FOUND_RESTAURANT_ORDER_CART_DETAIL);
        }

        List<RestaurantOrderDetailResponse> restaurantOrderDetails = new ArrayList<>();

        // 장바구니 Product 처리
        for (RestaurantOrderCartDetail cart : cartDetails) {
            Product product = cart.getProduct();

            // 상품 수량 체크 및 감소
            RestaurantValidator.checkedQuantity(product, cart.getQuantity());
            product.minusQuantity(cart.getQuantity());
            productRepository.save(product);

            RestaurantOrderDetail detail = RestaurantOrderDetail.builder()
                    .product(product)
                    .price(product.getDistributionPrice())
                    .quantity(cart.getQuantity())
                    .totalPrice(product.getDistributionPrice().multiply(BigDecimal.valueOf(cart.getQuantity())))
                    .build();
            detail.addOrder(restaurantOrder);

            restaurantOrderDetailRepository.save(detail);
            restaurantOrder.addTotalPrice(detail.getTotalPrice());

            NcpFileResponse findImage = fileService.findOne(product);
            restaurantOrderDetails.add(RestaurantOrderDetailResponse.of(detail, findImage));
        }

        restaurantOrderRepository.save(restaurantOrder);
        return RestaurantOrderSaveCodeResponse.of(restaurantOrder, restaurantOrderDetails);
    }

    /**
     * 발주 정보 저장 (Owner)
     */
    @Transactional
    public RestaurantOrderSaveResponse updateRestaurantOrder(Long id,
                                                             RestaurantOrderSaveRequest request,
                                                             Member member) {
        // Order 수정 로직
        RoleValidator.validateRole(member, MemberRole.OWNER);

        RestaurantOrder restaurantOrder = restaurantOrderRepository.findRestaurantOrderAddInfo(id)
                .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT_ORDER_NUMBER));

        RestaurantValidator.compareEntityMemberToMember(restaurantOrder, member);

        restaurantOrder.updateOrders(request.getDescription(), request.getRecipient(), request.getPhone());

        restaurantOrderRepository.save(restaurantOrder);

        List<RestaurantOrderDetailResponse> restaurantOrderDetails = restaurantOrder.getDetails().stream()
                .map(detail -> RestaurantOrderDetailResponse.of(detail, fileService.findOne(detail.getProduct())))
                .collect(Collectors.toList());

        // Cart Detail Data 차감 로직
        RestaurantOrderCart restaurantOrderCart =
                restaurantOrderCartRepository.findRestaurantAndMember(restaurantOrder.getRestaurant(), member)
                        .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT_ORDER_CART));

        for (RestaurantOrderCartDetail rocd : restaurantOrderCart.getRestaurantDetailOrders()) {
            RestaurantOrderCartDetail orderCartDetail = restaurantOrderCartDetailRepository.findCartAndProduct(
                            restaurantOrderCart, rocd.getProduct())
                    .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT_ORDER_CART_DETAIL));

            RestaurantOrderDetail orderDetail = restaurantOrderDetailRepository.findRestaurantOrderAndProduct(
                            restaurantOrder, orderCartDetail.getProduct())
                    .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT_ORDER_DETAIL));

            Long minusQuantity = RestaurantValidator.checkedQuantity(orderDetail, orderCartDetail);
            orderCartDetail.minusQuantity(minusQuantity);
            restaurantOrderCartDetailRepository.save(orderCartDetail);
        }

        return RestaurantOrderSaveResponse.of(restaurantOrder, restaurantOrderDetails);
    }

    /**
     * 발주 승인 (Admin)
     */
    @Transactional
    public RestaurantOrderResultResponse adminOrderApproval(Long id, Member member) {
        RoleValidator.validateRole(member, MemberRole.ADMIN);

        RestaurantOrder restaurantOrder = restaurantOrderRepository.findRestaurantOrderWaitingApproval(id)
                .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT_ORDER));

        RestaurantValidator.restaurantOrderStatusIsApproval(restaurantOrder);

        restaurantOrder.updateStatus(RestaurantOrderStatus.COMPLETED_APPROVAL);
        restaurantOrderRepository.save(restaurantOrder);

        orderCompleted(restaurantOrder);
        return RestaurantOrderResultResponse.of(restaurantOrder);
    }

    /**
     * 발주 승인 반려 (Admin)
     */
    @Transactional
    public RestaurantOrderResultResponse adminOrderRejectedApproval(Long id, Member member) {
        RoleValidator.validateRole(member, MemberRole.ADMIN);

        RestaurantOrder restaurantOrder = restaurantOrderRepository.findRestaurantOrderWaitingApproval(id)
                .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT_ORDER));

        RestaurantValidator.restaurantOrderStatusIsApproval(restaurantOrder);

        restaurantOrder.updateStatus(RestaurantOrderStatus.REJECTED_APPROVAL);
        restaurantOrderRepository.save(restaurantOrder);

        List<Product> products = new ArrayList<>();
        for (RestaurantOrderDetail orderDetail : restaurantOrder.getDetails()) {
            Product product = orderDetail.getProduct();
            product.plusQuantity(orderDetail.getQuantity());
            products.add(product);
        }
        productRepository.saveAll(products);

        return RestaurantOrderResultResponse.of(restaurantOrder);
    }

    /**
     * 발주 취소 (Owner)
     */
    @Transactional
    public RestaurantOrderResultResponse ownerOrderCancel(Long id, Member member) {
        RestaurantOrder restaurantOrder = restaurantOrderRepository.findRestaurantOrderWaitingApproval(id)
                .orElseThrow(() -> new BusinessException(Fail.NOT_FOUND_RESTAURANT_ORDER));

        Restaurant restaurant = restaurantOrder.getRestaurant();
        if (restaurant.getDeletedAt() != null) {
            throw new BusinessException(HttpResponse.Fail.NOT_FOUND_RESTAURANT);
        }

        RestaurantValidator.validateOwnership(member, restaurant);

        restaurantOrder.updateStatus(RestaurantOrderStatus.CANCELLED);
        restaurantOrderRepository.save(restaurantOrder);

        List<Product> products = new ArrayList<>();
        for (RestaurantOrderDetail orderDetail : restaurantOrder.getDetails()) {
            Product product = orderDetail.getProduct();
            product.plusQuantity(orderDetail.getQuantity());
            products.add(product);
        }
        productRepository.saveAll(products);

        return RestaurantOrderResultResponse.of(restaurantOrder);
    }

    @Transactional
    @Scheduled(cron = "0 0/30 * * * ?")
    public void restaurantOrderScheduler() {
        //TODO
        //FIXME AOP 적용 고려
        // - 스케줄러의 실행 log를 어떻게 찍을 것인가?

        long startTime = System.nanoTime();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nowTime = LocalDateTime.now().format(format);

        // 삭제 처리할 RestaurantOrder
        List<RestaurantOrder> orderToDeletes = restaurantOrderRepository.findOrderToDelete();

        for (RestaurantOrder restaurantOrder : orderToDeletes) {
            restaurantOrder.deleteEntity();
            restaurantOrder.updateStatus(RestaurantOrderStatus.CANCELLED);
            restaurantOrderRepository.save(restaurantOrder);

            for (RestaurantOrderDetail orderDetail : restaurantOrder.getDetails()) {
                Product product = orderDetail.getProduct();
                product.plusQuantity(orderDetail.getQuantity());
                productRepository.save(product);
            }
        }

        long endTime = System.nanoTime();
        log.info("\n[레스토랑 발주 관리 스케줄러 동작] \n"
                + "동작 시간 : %s \n".formatted(nowTime)
                + "소요 시간 : %sms \n".formatted((endTime - startTime) / 1_000_000)
                + "처리 데이터 수 : %s \n".formatted(orderToDeletes.size())
        );
    }

    private void orderCompleted(RestaurantOrder order) {
        order.updateStatus(RestaurantOrderStatus.COMPLETED);

        for (RestaurantOrderDetail detail : order.getDetails()) {
            RestaurantStock restaurantStock = restaurantStockRepository.findRestaurantAndProduct(order.getRestaurant(),
                            detail.getProduct())
                    .orElse(RestaurantStock.builder()
                            .product(detail.getProduct())
                            .restaurant(detail.getRestaurantOrder().getRestaurant())
                            .price(detail.getProduct().getDistributionPrice().multiply(BigDecimal.valueOf(1.1)))
                            .build());

            restaurantStock.plusQuantity(detail.getQuantity());
            restaurantStockRepository.save(restaurantStock);
        }
    }
}
