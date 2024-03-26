package com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundDetailRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundDetailCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.request.RestaurantOrderRefundRejectRequest;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response.RestaurantOrderRefundResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dto.response.RestaurantOrderRefundResultResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefundDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
@Transactional
public class RestaurantOrderRefundServiceTest {
    @InjectMocks
    private RestaurantOrderRefundServiceImpl restaurantOrderRefundService;
    @Mock
    private RestaurantOrderRefundRepository restaurantOrderRefundRepository;
    @Mock
    private RestaurantOrderRefundDetailRepository restaurantOrderRefundDetailRepository;
    @Mock
    private RestaurantStockRepository restaurantStockRepository;
    @Mock
    private RestaurantOrderRepository restaurantOrderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RestaurantRepository restaurantRepository;
    @Mock
    private FileService fileService;

    // Restaurant
    private String businessName = "레스쁘아";

    // RestaurantStock
    private Long stockQuantity1 = 100L;
    private Long stockQuantity2 = 50L;
    private Long productQuantity1 = 100L;
    private Long productQuantity2 = 50L;

    // Product
    private String productName1 = "1000억 막걸리 프리바이오";
    private String productName2 = "1000억 유산균막걸리";

    // RestaurantOrder
    private Long orderId = 1L;
    private RestaurantOrderStatus orderStatus = RestaurantOrderStatus.COMPLETED;
    private BigDecimal orderTotalPrice = BigDecimal.valueOf(22000);
    private String orderAddress = "서울특별시 종로구 종로8길 16";
    private String orderAddressDetail = "101";
    private String orderPostcode = "00001";
    private String fullAddress = orderAddress + " " + orderAddressDetail + " [" + orderPostcode + "]";

    // RestaurantOrderDetail
    private Long orderDetailQuantity1 = 2L;
    private BigDecimal orderDetailPrice1 = BigDecimal.valueOf(5000);
    private BigDecimal orderDetailTotalPrice1 = orderDetailPrice1.multiply(BigDecimal.valueOf(orderDetailQuantity1));
    private Long orderDetailQuantity2 = 2L;
    private BigDecimal orderDetailPrice2 = BigDecimal.valueOf(6000);
    private BigDecimal orderDetailTotalPrice2 = orderDetailPrice2.multiply(BigDecimal.valueOf(orderDetailQuantity2));

    // RestaurantRefund
    private Long refundId = 1L;
    private BigDecimal refundTotalPrice = BigDecimal.valueOf(11000);
    private String refundOwnerReason = "불필요 수량";
    private RestaurantOrderRefundStatus refundStatus = RestaurantOrderRefundStatus.COMPLETED_APPROVAL;

    // RestaurantRefundDetail
    Long refundDetailQuantity1 = 1L;
    BigDecimal refundDetailPrice1 = BigDecimal.valueOf(5000);
    BigDecimal refundDetailTotalPrice1 = refundDetailPrice1.multiply(BigDecimal.valueOf(refundDetailQuantity1));
    Long refundDetailQuantity2 = 1L;
    BigDecimal refundDetailPrice2 = BigDecimal.valueOf(6000);
    BigDecimal refundDetailTotalPrice2 = refundDetailPrice2.multiply(BigDecimal.valueOf(refundDetailQuantity2));

    private Map<String, Object> getMenuTest() {
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put("비빔밥", 8000);
        frame.put("불고기", 12000);
        return frame;
    }

    private Map<String, Object> getTimeTest() {
        Map<String, Object> allDayTime = new LinkedHashMap<>();

        allDayTime.put(TimeOption.HOLIDAY.toString(), true);
        allDayTime.put(TimeOption.ETC.toString(), "명절 당일만 휴업");

        TimeData timeData = TimeData.builder()
                .businessStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .breakBusinessStatus(true)
                .breakStartTime(LocalTime.of(15, 0))
                .breakEndTime(LocalTime.of(17, 0))
                .build();

        for (DayInfo value : DayInfo.values()) {
            allDayTime.put(value.toString(), timeData);
        }

        return allDayTime;
    }

    private Map<String, Object> getProvisionTest() {
        Map<String, Object> frame = new LinkedHashMap<>();

        for (Provision value : Provision.values()) {
            frame.put(value.toString(), true);
        }
        return frame;
    }

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final int page = 0;
    private final int size = 20;

    @Test
    @DisplayName("매장 발주 환불 목록 조회 (사장)")
    public void t1() {
        // given
        when(this.restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(this.getRestaurant()));
        when(this.restaurantOrderRefundRepository.findByRestaurantIdAndDeletedAtIsNullOrderByIdDesc(any(), any(Pageable.class))).thenReturn(this.getRestaurantOrderRefunds());
        when(this.restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(any())).thenReturn(this.getRestaurantOrderRefundDetails());

        // when
        Page<RestaurantOrderRefundResponse> refunds = this.restaurantOrderRefundService.getRestaurantOrderRefunds(getOwner(), getRestaurant().getId(), page, size);

        // then
        List<RestaurantOrderRefundResponse> content = refunds.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getRefundId()).isEqualTo(refundId);
        assertThat(content.get(0).getOrderId()).isEqualTo(orderId);
        assertThat(content.get(0).getBusinessName()).isEqualTo(businessName);
        assertThat(content.get(0).getFullAddress()).isEqualTo(fullAddress);
        assertThat(content.get(0).getOwnerReason()).isEqualTo(refundOwnerReason);
        assertThat(content.get(0).getRefundCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).getStatus()).isEqualTo(refundStatus);
        assertThat(content.get(0).getTotalPrice()).isEqualTo(refundTotalPrice);
        assertThat(content.get(0).getRefundDetails().get(0).getProductName()).isEqualTo(productName1);
        assertThat(content.get(0).getRefundDetails().get(0).getPrice()).isEqualTo(refundDetailPrice1);
        assertThat(content.get(0).getRefundDetails().get(0).getQuantity()).isEqualTo(refundDetailQuantity1);
        assertThat(content.get(0).getRefundDetails().get(0).getFile()).isEqualTo(null);
        assertThat(content.get(0).getRefundDetails().get(1).getProductName()).isEqualTo(productName2);
        assertThat(content.get(0).getRefundDetails().get(1).getPrice()).isEqualTo(refundDetailPrice2);
        assertThat(content.get(0).getRefundDetails().get(1).getQuantity()).isEqualTo(refundDetailQuantity2);
        assertThat(content.get(0).getRefundDetails().get(1).getFile()).isEqualTo(null);
    }

    @Test
    @DisplayName("매장 발주 환불 목록 조회 (사장) - 해당 매장이 존재하지 않으면 환불 목록 조회 불가")
    public void t1_1() {
        // given
        when(this.restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.getRestaurantOrderRefunds(getOwner(), getRestaurant().getId(), page, size);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 목록 조회 (사장) - 해당 매장의 사장이 본인이 아니면 환불 목록 조회 불가")
    public void t1_2() {
        // given
        when(this.restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(this.getRestaurant()));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.getRestaurantOrderRefunds(getOtherOwner(), getRestaurant().getId(), page, size);
        });

        // then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장)")
    public void t2() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));
        when(restaurantOrderRefundRepository.existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(any(), any())).thenReturn(false);
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(this.getRestaurantStocks1()));
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product2.getId())).thenReturn(Optional.of(this.getRestaurantStocks2()));
        when(restaurantOrderRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(this.getRestaurantOrder()));
        when(productRepository.findByIdAndDeletedAtIsNull(product1.getId())).thenReturn(Optional.of(this.getProduct1()));
        when(productRepository.findByIdAndDeletedAtIsNull(product2.getId())).thenReturn(Optional.of(this.getProduct2()));

        when(restaurantOrderRefundRepository.save(any(RestaurantOrderRefund.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<RestaurantOrderRefundDetail> savedRefundDetails = new ArrayList<>();
        when(restaurantOrderRefundDetailRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    savedRefundDetails.addAll(invocation.getArgument(0));
                    return invocation.getArgument(0);
                });
        List<RestaurantStock> savedStocks = new ArrayList<>();
        when(restaurantStockRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    savedStocks.addAll(invocation.getArgument(0));
                    return invocation.getArgument(0);
                });

        ArgumentCaptor<List<RestaurantStock>> restaurantStocksCaptor = ArgumentCaptor.forClass(List.class);

        // When
        RestaurantOrderRefundResponse response = restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);

        // then
        verify(restaurantStockRepository).saveAll(restaurantStocksCaptor.capture());

        List<RestaurantStock> savedRestaurantStocks = restaurantStocksCaptor.getValue();

        assertThat(savedRestaurantStocks.get(0).getQuantity()).isEqualTo(stockQuantity1 - refundDetailQuantity1);
        assertThat(savedRestaurantStocks.get(1).getQuantity()).isEqualTo(stockQuantity2 - refundDetailQuantity2);

        verify(restaurantOrderRefundRepository, times(1)).save(any(RestaurantOrderRefund.class));
        assertEquals(2, savedRefundDetails.size());
        assertEquals(2, savedStocks.size());

        //assertThat(response.getRefundId()).isEqualTo(refundId);
        assertThat(response.getOrderId()).isEqualTo(orderId);
        assertThat(response.getBusinessName()).isEqualTo(businessName);
        assertThat(response.getFullAddress()).isEqualTo(fullAddress);
        assertThat(response.getOwnerReason()).isEqualTo(refundOwnerReason);
        //assertThat(response.getRefundCreatedAt()).isEqualTo(createdAt);
        assertThat(response.getStatus()).isEqualTo(RestaurantOrderRefundStatus.WAITING_APPROVAL); // 새 환불은 환불승인대기 상태로 생성된다.
        assertThat(response.getTotalPrice()).isEqualTo(refundTotalPrice);
        assertThat(response.getRefundDetails().get(0).getProductName()).isEqualTo(productName1);
        assertThat(response.getRefundDetails().get(0).getPrice()).isEqualTo(refundDetailPrice1);
        assertThat(response.getRefundDetails().get(0).getQuantity()).isEqualTo(refundDetailQuantity1);
        assertThat(response.getRefundDetails().get(0).getFile()).isEqualTo(null);
        assertThat(response.getRefundDetails().get(1).getProductName()).isEqualTo(productName2);
        assertThat(response.getRefundDetails().get(1).getPrice()).isEqualTo(refundDetailPrice2);
        assertThat(response.getRefundDetails().get(1).getQuantity()).isEqualTo(refundDetailQuantity2);
        assertThat(response.getRefundDetails().get(1).getFile()).isEqualTo(null);
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 해당 매장이 존재하지 않으면 환불 불가")
    public void t2_0_1() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(RestaurantOrderStatus.CANCELLED) // 발주완료 이외의 상태로 변경
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 해당 매장의 사장이 본인이 아니면 환불 불가")
    public void t2_0_2() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(RestaurantOrderStatus.CANCELLED) // 발주완료 이외의 상태로 변경
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOtherOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 1. 발주 완료 이외의 상태는 환불 불가")
    public void t2_1() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(RestaurantOrderStatus.CANCELLED) // 발주완료 이외의 상태로 변경
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 2. 환불은 발주 일자로 부터 7일이 넘으면 환불 불가")
    public void t2_2() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(LocalDateTime.now().minusDays(8)) // 발주일자로부터 7일 넘기도록 수정
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 3. 환불 제품이 존재하지 않으면 환불 불가")
    public void t2_3() {
        // given
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 4. 해당 주문에 진행 중인 환불이 있으면 환불 불가")
    public void t2_4() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));
        // 진행중인 환불 있도록 설정
        when(restaurantOrderRefundRepository.existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(any(), any())).thenReturn(true);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 5. 환불 요청의 환불 개수가 0개 이면 환불 불가")
    public void t2_5() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(0L) // 환불 개수 0개 설정
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));
        when(restaurantOrderRefundRepository.existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(any(), any())).thenReturn(false);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 6. 주문의 환불 가능한 재고 보다 환불 개수가 많으면 환불 불가")
    public void t2_6() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(0L) // 환불 가능 개수 적게 설정
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));
        when(restaurantOrderRefundRepository.existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(any(), any())).thenReturn(false);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 7. 매장의 재고 보다 환불 개수가 많으면 환불 불가")
    public void t2_7() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        // 매장의 자고의 수량을 적게 설절
        RestaurantStock restaurantStock1 = getRestaurantStocks1();
        restaurantStock1 = restaurantStock1.toBuilder()
                .quantity(0L)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));
        when(restaurantOrderRefundRepository.existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(any(), any())).thenReturn(false);
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(restaurantStock1));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 해당 주문이 존재하지 않으면 환불 불가")
    public void t2_8() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));
        when(restaurantOrderRefundRepository.existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(any(), any())).thenReturn(false);
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(this.getRestaurantStocks1()));
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product2.getId())).thenReturn(Optional.of(this.getRestaurantStocks2()));
        when(restaurantOrderRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_ORDER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 해당 제품이 존재하지 않으면 환불 불가")
    public void t2_9() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));
        when(restaurantOrderRefundRepository.existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(any(), any())).thenReturn(false);
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(this.getRestaurantStocks1()));
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product2.getId())).thenReturn(Optional.of(this.getRestaurantStocks2()));
        when(restaurantOrderRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(this.getRestaurantOrder()));
        when(productRepository.findByIdAndDeletedAtIsNull(product1.getId())).thenReturn(Optional.of(this.getProduct1()));
        when(productRepository.findByIdAndDeletedAtIsNull(product2.getId())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 추가 (사장) - 해당 매장의 재고가 존재하지 않으면 환불 불가")
    public void t2_10() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        RestaurantOrderRefundDetailCreateRequest detailRequest1 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product1.getId())
                .price(refundDetailPrice1)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity1)
                .build();

        RestaurantOrderRefundDetailCreateRequest detailRequest2 = RestaurantOrderRefundDetailCreateRequest.builder()
                .productId(product2.getId())
                .price(refundDetailPrice2)
                .possibleQuantity(10L)
                .quantity(refundDetailQuantity2)
                .build();

        RestaurantOrderRefundCreateRequest request = RestaurantOrderRefundCreateRequest.builder()
                .restaurantId(restaurant.getId())
                .orderId(restaurantOrder.getId())
                .orderDate(restaurantOrder.getCreatedAt())
                .status(restaurantOrder.getOrderStatus())
                .ownerReason(refundOwnerReason)
                .refundDetails(List.of(detailRequest1, detailRequest2))
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(restaurant));
        when(restaurantOrderRefundRepository.existsByRestaurantOrderIdAndStatusAndDeletedAtIsNull(any(), any())).thenReturn(false);
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(this.getRestaurantStocks1()));
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product2.getId())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.createRestaurantOrderRefund(getOwner(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_STOCK.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_STOCK.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 취소 (사장)")
    public void t3() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));
        when(restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(restaurantOrderRefund)).thenReturn(getRestaurantOrderRefundDetails());
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(this.getRestaurantStocks1()));
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product2.getId())).thenReturn(Optional.of(this.getRestaurantStocks2()));

        when(restaurantOrderRefundRepository.save(any(RestaurantOrderRefund.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<RestaurantStock> savedStocks = new ArrayList<>();
        when(restaurantStockRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    savedStocks.addAll(invocation.getArgument(0));
                    return invocation.getArgument(0);
                });

        ArgumentCaptor<RestaurantOrderRefund> restaurantOrderRefundCaptor = ArgumentCaptor.forClass(RestaurantOrderRefund.class);
        ArgumentCaptor<List<RestaurantStock>> restaurantStocksCaptor = ArgumentCaptor.forClass(List.class);

        // When
        RestaurantOrderRefundResultResponse response = restaurantOrderRefundService.cancelRestaurantOrderRefund(getOwner(), restaurantOrderRefund.getId());

        // then
        verify(restaurantOrderRefundRepository, times(1)).save(any(RestaurantOrderRefund.class));
        assertEquals(2, savedStocks.size());

        verify(restaurantOrderRefundRepository).save(restaurantOrderRefundCaptor.capture());
        verify(restaurantStockRepository).saveAll(restaurantStocksCaptor.capture());

        RestaurantOrderRefund savedRestaurantOrderRefund = restaurantOrderRefundCaptor.getValue();
        List<RestaurantStock> savedRestaurantStocks = restaurantStocksCaptor.getValue();

        assertThat(savedRestaurantOrderRefund.getStatus()).isEqualTo(RestaurantOrderRefundStatus.CANCELLED);
        assertThat(savedRestaurantStocks.get(0).getQuantity()).isEqualTo(stockQuantity1 + refundDetailQuantity1);
        assertThat(savedRestaurantStocks.get(1).getQuantity()).isEqualTo(stockQuantity2 + refundDetailQuantity2);

        assertThat(response.getId()).isEqualTo(refundId);
        assertThat(response.getOwnerReason()).isEqualTo(refundOwnerReason);
        assertThat(response.getStatus()).isEqualTo(RestaurantOrderRefundStatus.CANCELLED);
    }

    @Test
    @DisplayName("매장 발주 환불 취소 (사장) - 해당 매장 발주 환불이 존재하지 않음")
    public void t3_0() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.cancelRestaurantOrderRefund(getOwner(), restaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 취소 (사장) - 해당 매장의 사장이 본인이 아니면 환불 취소 불가")
    public void t3_1() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.cancelRestaurantOrderRefund(getOtherOwner(), restaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.FORBIDDEN.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.FORBIDDEN.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 취소 (사장) - 환불 승인 대기 이외에는 환불 취소 불가")
    public void t3_2() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.COMPLETED)
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.cancelRestaurantOrderRefund(getOwner(), finalRestaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_CANCEL_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_CANCEL_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 취소 (사장) - 환불 상세 내역이 없다면 환불 취소 불가")
    public void t3_3() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));
        when(restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(restaurantOrderRefund)).thenReturn(List.of());

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.cancelRestaurantOrderRefund(getOwner(), finalRestaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND_DETAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND_DETAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 취소 (사장) - 해당 매장의 재고가 존재하지 않으면 환불 불가")
    public void t3_4() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));
        when(restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(restaurantOrderRefund)).thenReturn(getRestaurantOrderRefundDetails());
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(this.getRestaurantStocks1()));
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product2.getId())).thenReturn(Optional.empty());

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.cancelRestaurantOrderRefund(getOwner(), finalRestaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_STOCK.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_STOCK.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 발주 환불 목록 조회 (관리자)")
    public void t4() {
        // given
        when(this.restaurantOrderRefundRepository.findByDeletedAtIsNullOrderByIdDesc(any(Pageable.class))).thenReturn(this.getRestaurantOrderRefunds());
        when(this.restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(any())).thenReturn(this.getRestaurantOrderRefundDetails());

        // when
        Page<RestaurantOrderRefundResponse> refunds = this.restaurantOrderRefundService.getAllRestaurantOrderRefunds(page, size);

        // then
        List<RestaurantOrderRefundResponse> content = refunds.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getRefundId()).isEqualTo(refundId);
        assertThat(content.get(0).getOrderId()).isEqualTo(orderId);
        assertThat(content.get(0).getBusinessName()).isEqualTo(businessName);
        assertThat(content.get(0).getFullAddress()).isEqualTo(fullAddress);
        assertThat(content.get(0).getOwnerReason()).isEqualTo(refundOwnerReason);
        assertThat(content.get(0).getRefundCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).getStatus()).isEqualTo(refundStatus);
        assertThat(content.get(0).getTotalPrice()).isEqualTo(refundTotalPrice);
        assertThat(content.get(0).getRefundDetails().get(0).getProductName()).isEqualTo(productName1);
        assertThat(content.get(0).getRefundDetails().get(0).getPrice()).isEqualTo(refundDetailPrice1);
        assertThat(content.get(0).getRefundDetails().get(0).getQuantity()).isEqualTo(refundDetailQuantity1);
        assertThat(content.get(0).getRefundDetails().get(0).getFile()).isEqualTo(null);
        assertThat(content.get(0).getRefundDetails().get(1).getProductName()).isEqualTo(productName2);
        assertThat(content.get(0).getRefundDetails().get(1).getPrice()).isEqualTo(refundDetailPrice2);
        assertThat(content.get(0).getRefundDetails().get(1).getQuantity()).isEqualTo(refundDetailQuantity2);
        assertThat(content.get(0).getRefundDetails().get(1).getFile()).isEqualTo(null);
    }

    @Test
    @DisplayName("매장 환불 승인 (관리자)")
    public void t5() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .restaurantOrderRefundDetails(getRestaurantOrderRefundDetails())
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));
        when(restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(restaurantOrderRefund)).thenReturn(getRestaurantOrderRefundDetails());

        when(restaurantOrderRefundRepository.save(any(RestaurantOrderRefund.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Product> savedProducts1 = new ArrayList<>();
        when(productRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    savedProducts1.addAll(invocation.getArgument(0));
                    return invocation.getArgument(0);
                });

        ArgumentCaptor<RestaurantOrderRefund> restaurantOrderRefundCaptor = ArgumentCaptor.forClass(RestaurantOrderRefund.class);
        ArgumentCaptor<List<Product>> productsCaptor = ArgumentCaptor.forClass(List.class);

        // When
        RestaurantOrderRefundResultResponse response = restaurantOrderRefundService.approvalRestaurantOrderRefund(restaurantOrderRefund.getId());

        // then
        verify(restaurantOrderRefundRepository, times(2)).save(any(RestaurantOrderRefund.class));
        assertEquals(2, savedProducts1.size());

        verify(productRepository).saveAll(productsCaptor.capture());

        List<Product> savedProducts = productsCaptor.getValue();

        assertThat(savedProducts.get(0).getQuantity()).isEqualTo(productQuantity1 + refundDetailQuantity1);
        assertThat(savedProducts.get(1).getQuantity()).isEqualTo(productQuantity2 + refundDetailQuantity2);

        assertThat(response.getId()).isEqualTo(refundId);
        assertThat(response.getTotalPrice()).isEqualTo(refundTotalPrice);
        assertThat(response.getOwnerReason()).isEqualTo(refundOwnerReason);
        assertThat(response.getAdminReason()).isEqualTo(null);
        assertThat(response.getStatus()).isEqualTo(RestaurantOrderRefundStatus.COMPLETED);
    }

    @Test
    @DisplayName("매장 환불 승인 (관리자) - 해당 매장 발주 환불이 존재하지 않음")
    public void t5_1() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .restaurantOrderRefundDetails(getRestaurantOrderRefundDetails())
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.empty());

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.approvalRestaurantOrderRefund(finalRestaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 환불 승인 (관리자) - 환불 승인 대기 이외에는 환불 승인 불가")
    public void t5_2() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .restaurantOrderRefundDetails(getRestaurantOrderRefundDetails())
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.approvalRestaurantOrderRefund(finalRestaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_APPROVAL_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_APPROVAL_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 환불 승인 (관리자) - 환불 상세 내역이 없다면 환불 승인 불가")
    public void t5_3() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.approvalRestaurantOrderRefund(finalRestaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND_DETAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND_DETAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 환불 승인 (관리자) - 매장 상세 내역의 제품이 삭제 상태이면 환불 완료 불가")
    public void t5_4() {
        // given
        Product product1 = getProduct1();
        product1 = product1.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();
        RestaurantOrderRefundDetail detail1 = getRestaurantOrderRefundDetail1();
        detail1 = detail1.toBuilder()
                .product(product1)
                .build();
        List<RestaurantOrderRefundDetail> details = List.of(detail1, getRestaurantOrderRefundDetail2());
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .restaurantOrderRefundDetails(details)
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));
        when(restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(restaurantOrderRefund)).thenReturn(details);

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.approvalRestaurantOrderRefund(finalRestaurantOrderRefund.getId());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 환불 반려 (관리자)")
    public void t6() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();

        RestaurantOrderRefundRejectRequest request = RestaurantOrderRefundRejectRequest.builder()
                .adminReason("관리자의 권한으로 환불 반려")
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));
        when(restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(restaurantOrderRefund)).thenReturn(getRestaurantOrderRefundDetails());
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(this.getRestaurantStocks1()));
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product2.getId())).thenReturn(Optional.of(this.getRestaurantStocks2()));

        when(restaurantOrderRefundRepository.save(any(RestaurantOrderRefund.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<RestaurantStock> savedStocks = new ArrayList<>();
        when(restaurantStockRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    savedStocks.addAll(invocation.getArgument(0));
                    return invocation.getArgument(0);
                });

        ArgumentCaptor<RestaurantOrderRefund> restaurantOrderRefundCaptor = ArgumentCaptor.forClass(RestaurantOrderRefund.class);
        ArgumentCaptor<List<RestaurantStock>> restaurantStocksCaptor = ArgumentCaptor.forClass(List.class);

        // When
        RestaurantOrderRefundResultResponse response = restaurantOrderRefundService.rejectRestaurantOrderRefund(restaurantOrderRefund.getId(), request);

        // then
        verify(restaurantOrderRefundRepository, times(1)).save(any(RestaurantOrderRefund.class));
        assertEquals(2, savedStocks.size());

        verify(restaurantOrderRefundRepository).save(restaurantOrderRefundCaptor.capture());
        verify(restaurantStockRepository).saveAll(restaurantStocksCaptor.capture());

        RestaurantOrderRefund savedRestaurantOrderRefund = restaurantOrderRefundCaptor.getValue();
        List<RestaurantStock> savedRestaurantStocks = restaurantStocksCaptor.getValue();

        assertThat(savedRestaurantOrderRefund.getStatus()).isEqualTo(RestaurantOrderRefundStatus.REJECTED_APPROVAL);
        assertThat(savedRestaurantStocks.get(0).getQuantity()).isEqualTo(stockQuantity1 + refundDetailQuantity1);
        assertThat(savedRestaurantStocks.get(1).getQuantity()).isEqualTo(stockQuantity2 + refundDetailQuantity2);

        assertThat(response.getId()).isEqualTo(refundId);
        assertThat(response.getTotalPrice()).isEqualTo(refundTotalPrice);
        assertThat(response.getOwnerReason()).isEqualTo(refundOwnerReason);
        assertThat(response.getAdminReason()).isEqualTo("관리자의 권한으로 환불 반려");
        assertThat(response.getStatus()).isEqualTo(RestaurantOrderRefundStatus.REJECTED_APPROVAL);
    }

    @Test
    @DisplayName("매장 환불 반려 (관리자) - 해당 매장 발주 환불이 존재하지 않음")
    public void t6_1() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();

        RestaurantOrderRefundRejectRequest request = RestaurantOrderRefundRejectRequest.builder()
                .adminReason("관리자의 권한으로 환불 반려")
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.empty());

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.rejectRestaurantOrderRefund(finalRestaurantOrderRefund.getId(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 환불 반려 (관리자) - 환불 승인 대기 이외에는 환불 승인 불가")
    public void t6_2() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();

        RestaurantOrderRefundRejectRequest request = RestaurantOrderRefundRejectRequest.builder()
                .adminReason("관리자의 권한으로 환불 반려")
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.rejectRestaurantOrderRefund(restaurantOrderRefund.getId(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_REJECT_FAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.RESTAURANT_REFUND_REJECT_FAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 환불 반려 (관리자) - 환불 상세 내역이 없다면 환불 승인 불가")
    public void t6_3() {
        // given
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();

        RestaurantOrderRefundRejectRequest request = RestaurantOrderRefundRejectRequest.builder()
                .adminReason("관리자의 권한으로 환불 반려")
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));
        when(restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(restaurantOrderRefund)).thenReturn(List.of());

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.rejectRestaurantOrderRefund(finalRestaurantOrderRefund.getId(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND_DETAIL.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_REFUND_DETAIL.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("매장 환불 반려 (관리자) - 환불 상세 내역이 없다면 환불 승인 불가")
    public void t6_4() {
        // given
        Product product1 = getProduct1();
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        restaurantOrderRefund = restaurantOrderRefund.toBuilder()
                .status(RestaurantOrderRefundStatus.WAITING_APPROVAL)
                .build();

        RestaurantOrderRefundRejectRequest request = RestaurantOrderRefundRejectRequest.builder()
                .adminReason("관리자의 권한으로 환불 반려")
                .build();

        when(restaurantOrderRefundRepository.findByIdAndDeletedAtIsNull(restaurantOrderRefund.getId())).thenReturn(Optional.of(restaurantOrderRefund));
        when(restaurantOrderRefundDetailRepository.findByRestaurantOrderRefundAndDeletedAtIsNull(restaurantOrderRefund)).thenReturn(getRestaurantOrderRefundDetails());
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product1.getId())).thenReturn(Optional.of(this.getRestaurantStocks1()));
        when(restaurantStockRepository.findByRestaurantIdAndProductIdAndDeletedAtIsNull(restaurant.getId(), product2.getId())).thenReturn(Optional.empty());

        // when
        RestaurantOrderRefund finalRestaurantOrderRefund = restaurantOrderRefund;
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            restaurantOrderRefundService.rejectRestaurantOrderRefund(finalRestaurantOrderRefund.getId(), request);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_STOCK.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_RESTAURANT_STOCK.getMessage(), exception.getMessage());
    }

    private Page<RestaurantOrderRefund> getRestaurantOrderRefunds() {
        List<RestaurantOrderRefund> list = List.of(this.getRestaurantOrderRefund());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<RestaurantOrderRefund>(list, pageable, list.size());
    }

    private List<RestaurantOrderRefundDetail> getRestaurantOrderRefundDetails() {
        return List.of(this.getRestaurantOrderRefundDetail1(),
                this.getRestaurantOrderRefundDetail2());
    }

    private Member getOwner() {
        return Member.builder()
                .id(1L)
                .email("owner1@af.shop").provider(ProviderType.KAKAO).name("owner1").nickname("owner1").role(MemberRole.OWNER).phone(1012345687L).certifyAt(LocalDate.now()).agreedToServiceUse(true).agreedToServicePolicy(true).agreedToServicePolicyUse(true)
                .createdAt(createdAt)
                .build();
    }

    private Member getOtherOwner() {
        return Member.builder()
                .id(2L)
                .email("owner2@af.shop").provider(ProviderType.KAKAO).name("owner2").nickname("owner2").role(MemberRole.OWNER).phone(1012345687L).certifyAt(LocalDate.now()).agreedToServiceUse(true).agreedToServicePolicy(true).agreedToServicePolicyUse(true)
                .createdAt(createdAt)
                .build();
    }

    private Restaurant getRestaurant() {
        Member owner = getOwner();
        return Restaurant.builder()
                .id(1L)
                .member(owner).category("음식점").name("레스쁘아").address("서울특별시 종로구 종로8길 16").location(Restaurant.genPoint(37.569343, 126.983857)).contact(212345678L).menu(getMenuTest()).time(getTimeTest()).provision(getProvisionTest())
                .businessName(businessName)
                .businessNumber("101-10-10001").addressDetail("101").postcode("00001")
                .createdAt(createdAt)
                .build();

    }

    private CategoryClass getCategoryClass() {
        return CategoryClass.builder()
                .id(1L)
                .firstName("전통주")
                .createdAt(createdAt)
                .build();
    }

    private Category getCategory() {
        CategoryClass categoryClass = getCategoryClass();
        return Category.builder()
                .id(1L)
                .categoryClass(categoryClass).lastName("탁주/막걸리")
                .createdAt(createdAt)
                .build();
    }

    private Maker getMaker() {
        return Maker.builder()
                .id(1L)
                .name("(주)국순당").address("강원도 횡성군 둔내면 강변로 975").region("강원도 횡성군").detail("101")
                .createdAt(createdAt)
                .build();
    }

    private Product getProduct1() {
        Maker maker = getMaker();
        Category category = getCategory();

        return Product.builder()
                .id(1L)
                .name(productName1)
                .price(BigDecimal.valueOf(3500))
                .quantity(productQuantity1)
                .alcohol(5D).ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수").sweet(3L).sour(4L).cool(3L).body(3L).balance(0L).incense(0L).throat(0L).maker(maker).distributionPrice(BigDecimal.valueOf(3850.0)).category(category)
                .createdAt(createdAt)
                .build();
    }

    private Product getProduct2() {
        Maker maker = getMaker();
        Category category = getCategory();

        return Product.builder()
                .id(2L)
                .name(productName2)
                .price(BigDecimal.valueOf(3200))
                .quantity(productQuantity2)
                .alcohol(5D).ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수").sweet(3L).sour(5L).cool(5L).body(3L).balance(0L).incense(0L).throat(0L).maker(maker).distributionPrice(BigDecimal.valueOf(3520.0)).category(category)
                .createdAt(createdAt)
                .build();
    }

    private RestaurantStock getRestaurantStocks1() {
        Product product1 = getProduct1();
        Restaurant restaurant = getRestaurant();

        return RestaurantStock.builder()
                .id(1L)
                .product(product1)
                .quantity(stockQuantity1)
                .restaurant(restaurant)
                .createdAt(createdAt)
                .build();
    }

    private RestaurantStock getRestaurantStocks2() {
        Product product2 = getProduct2();
        Restaurant restaurant = getRestaurant();

        return RestaurantStock.builder()
                .id(2L)
                .product(product2)
                .quantity(stockQuantity2)
                .restaurant(restaurant)
                .createdAt(createdAt)
                .build();
    }

    private RestaurantOrder getRestaurantOrder() {
        Restaurant restaurant = getRestaurant();
        Member owner = restaurant.getMember();

        return RestaurantOrder.builder()
                .id(orderId)
                .orderStatus(orderStatus)
                .totalPrice(orderTotalPrice)
                .address(orderAddress)
                .addressDetail(orderAddressDetail)
                .description("조심히 배송 부탁드립니다.")
                .postcode(orderPostcode)
                .recipient(owner.getName()).phone(owner.getPhone()).restaurant(restaurant).member(restaurant.getMember())
                .createdAt(createdAt)
                .build();
    }

    private RestaurantOrderDetail getRestaurantOrderDetail1() {
        Product product1 = getProduct1();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        return RestaurantOrderDetail.builder()
                .id(1L)
                .quantity(orderDetailQuantity1)
                .price(orderDetailPrice1)
                .totalPrice(orderDetailTotalPrice1)
                .restaurantOrder(restaurantOrder)
                .product(product1)
                .createdAt(createdAt)
                .build();
    }

    private RestaurantOrderDetail getRestaurantOrderDetail2() {
        Product product2 = getProduct2();
        RestaurantOrder restaurantOrder = getRestaurantOrder();

        return RestaurantOrderDetail.builder()
                .id(2L)
                .quantity(orderDetailQuantity2)
                .price(orderDetailPrice2)
                .totalPrice(orderDetailTotalPrice2)
                .restaurantOrder(restaurantOrder)
                .product(product2)
                .createdAt(createdAt)
                .build();
    }

    private RestaurantOrderRefund getRestaurantOrderRefund() {
        RestaurantOrder restaurantOrder = getRestaurantOrder();


        return RestaurantOrderRefund.builder()
                .id(refundId)
                .restaurantOrder(restaurantOrder)
                .totalPrice(refundTotalPrice)
                .ownerReason(refundOwnerReason)
                .status(refundStatus)
                .restaurant(restaurantOrder.getRestaurant())
                .createdAt(createdAt)
                .build();
    }

    private RestaurantOrderRefundDetail getRestaurantOrderRefundDetail1() {
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        RestaurantOrderDetail restaurantOrderDetail1 = getRestaurantOrderDetail1();
        Product product1 = restaurantOrderDetail1.getProduct();

        return RestaurantOrderRefundDetail.builder()
                .id(1L)
                .restaurantOrderRefund(restaurantOrderRefund)
                .product(product1)
                .quantity(refundDetailQuantity1)
                .price(refundDetailPrice1)
                .totalPrice(refundDetailTotalPrice1)
                .createdAt(createdAt)
                .build();
    }

    private RestaurantOrderRefundDetail getRestaurantOrderRefundDetail2() {
        RestaurantOrderRefund restaurantOrderRefund = getRestaurantOrderRefund();
        RestaurantOrderDetail restaurantOrderDetail2 = getRestaurantOrderDetail2();
        Product product2 = restaurantOrderDetail2.getProduct();

        return RestaurantOrderRefundDetail.builder()
                .id(2L)
                .restaurantOrderRefund(restaurantOrderRefund)
                .product(product2)
                .quantity(refundDetailQuantity2)
                .price(refundDetailPrice2)
                .totalPrice(refundDetailTotalPrice2)
                .createdAt(createdAt)
                .build();
    }
}
