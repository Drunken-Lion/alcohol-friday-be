package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.dto.response.RestaurantOrderProductListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dao.RestaurantOrderRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.OwnerRestaurantOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.OwnerRestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.dto.response.RestaurantOrderListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrder;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.entity.RestaurantOrderDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.dao.RestaurantOrderRefundRepository;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefund;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.entity.RestaurantOrderRefundDetail;
import com.drunkenlion.alcoholfriday.domain.admin.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class RestaurantOrderServiceTest {
    @InjectMocks
    private RestaurantOrderServiceImpl restaurantOrderService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantOrderRepository restaurantOrderRepository;

    @Mock
    private RestaurantOrderRefundRepository restaurantOrderRefundRepository;
    @Mock
    private ProductRepository productRepository;

    @Mock
    private FileService fileService;

    // Restaurant
    private Long restaurantId = 1L;
    private String businessName = "레스쁘아";

    // Product
    private Long productId1 = 1L;
    private String productName1 = "1000억 막걸리 프리바이오";
    private Long productId2 = 2L;
    private String productName2 = "1000억 유산균막걸리";
    private String makerName = "(주)국순당";

    // RestaurantOrder
    private Long orderId = 1L;
    private RestaurantOrderStatus orderStatus = RestaurantOrderStatus.COMPLETED;
    private BigDecimal orderTotalPrice = BigDecimal.valueOf(22000);
    private String orderAddress = "서울특별시 종로구 종로8길 16";
    private String orderAddressDetail = "101";
    private String orderPostcode = "00001";
    private String orderDescription = "조심히 배송 부탁드립니다.";

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

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final int page = 0;
    private final int size = 20;

    @Test
    @DisplayName("모든 발주 내역 조회")
    public void getRestaurantOrdersTest() {
        // given
        when(restaurantOrderRepository.findAllRestaurantOrders(any(Pageable.class)))
                .thenReturn(getRestaurantOrders());

        when(fileService.findOne(any())).thenReturn(null);

        // when
        Page<RestaurantOrderListResponse> orders =
                restaurantOrderService.getRestaurantOrdersByAdminOrStoreManager(getAdmin(), page, size);

        // then
        List<RestaurantOrderListResponse> content = orders.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(orderId);
        assertThat(content.get(0).getOrderStatus()).isEqualTo(orderStatus.getName());
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).getBusinessName()).isEqualTo(businessName);
        assertThat(content.get(0).getAddress()).isEqualTo(orderAddress);
        assertThat(content.get(0).getAddressDetail()).isEqualTo(orderAddressDetail);
        assertThat(content.get(0).getPostcode()).isEqualTo(orderPostcode);
        assertThat(content.get(0).getDescription()).isEqualTo(orderDescription);

        List<RestaurantOrderDetailResponse> details = content.get(0).getDetails();

        assertThat(details).isInstanceOf(List.class);
        assertThat(details.size()).isEqualTo(2);
        assertThat(details.get(0).getId()).isEqualTo(productId1);
        assertThat(details.get(0).getName()).isEqualTo(productName1);
        assertThat(details.get(0).getMakerName()).isEqualTo(makerName);
        assertThat(details.get(0).getPrice()).isEqualTo(orderDetailPrice1);
        assertThat(details.get(0).getOrderQuantity()).isEqualTo(orderDetailQuantity1);
        assertThat(details.get(0).getFile()).isEqualTo(null);

        assertThat(details.get(1).getId()).isEqualTo(productId2);
        assertThat(details.get(1).getName()).isEqualTo(productName2);
        assertThat(details.get(1).getMakerName()).isEqualTo(makerName);
        assertThat(details.get(1).getPrice()).isEqualTo(orderDetailPrice2);
        assertThat(details.get(1).getOrderQuantity()).isEqualTo(orderDetailQuantity2);
        assertThat(details.get(1).getFile()).isEqualTo(null);
    }

    @Test
    @DisplayName("사장의 발주 내역 조회")
    public void getRestaurantOrdersByOwnerTest() {
        // given
        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(getRestaurant()));

        when(restaurantOrderRepository.findRestaurantOrdersByOwner(any(), any(), any(Pageable.class)))
                .thenReturn(getRestaurantOrders());

        when(restaurantOrderRefundRepository.findRefundByRestaurantOrderId(any()))
                .thenReturn(getRestaurantOrderRefunds());

        when(fileService.findOne(any())).thenReturn(null);

        // when
        Page<OwnerRestaurantOrderListResponse> orders =
                restaurantOrderService.getRestaurantOrdersByOwner(getOwner(), restaurantId, page, size);

        // then
        List<OwnerRestaurantOrderListResponse> content = orders.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(orderId);
        assertThat(content.get(0).getOrderStatus()).isEqualTo(orderStatus.getName());
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).getBusinessName()).isEqualTo(businessName);
        assertThat(content.get(0).getAddress()).isEqualTo(orderAddress);
        assertThat(content.get(0).getAddressDetail()).isEqualTo(orderAddressDetail);
        assertThat(content.get(0).getPostcode()).isEqualTo(orderPostcode);
        assertThat(content.get(0).getDescription()).isEqualTo(orderDescription);

        List<OwnerRestaurantOrderDetailResponse> details = content.get(0).getOrderDetails();

        assertThat(details).isInstanceOf(List.class);
        assertThat(details.size()).isEqualTo(2);
        assertThat(details.get(0).getId()).isEqualTo(productId1);
        assertThat(details.get(0).getProductName()).isEqualTo(productName1);
        assertThat(details.get(0).getPrice()).isEqualTo(orderDetailPrice1);
        assertThat(details.get(0).getQuantity()).isEqualTo(orderDetailQuantity1);
        assertThat(details.get(0).getRefundQuantity()).isEqualTo(orderDetailQuantity1 - refundDetailQuantity1);
        assertThat(details.get(0).getFile()).isEqualTo(null);

        assertThat(details.get(1).getId()).isEqualTo(productId2);
        assertThat(details.get(1).getProductName()).isEqualTo(productName2);
        assertThat(details.get(1).getPrice()).isEqualTo(orderDetailPrice2);
        assertThat(details.get(1).getQuantity()).isEqualTo(orderDetailQuantity2);
        assertThat(details.get(1).getRefundQuantity()).isEqualTo(orderDetailQuantity2 - refundDetailQuantity2);
        assertThat(details.get(1).getFile()).isEqualTo(null);
    }

    @Test
    @DisplayName("발주를 위한 제품 목록")
    public void getRestaurantOrderProductsTest() {

        when(productRepository.findAllByDeletedAtIsNull(any())).thenReturn(getProducts());
        when(fileService.findOne(any())).thenReturn(null);

        // when
        Page<RestaurantOrderProductListResponse> products =
                restaurantOrderService.getRestaurantOrderProducts(page, size, getOwner());

        // then
        List<RestaurantOrderProductListResponse> content = products.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(0).getId()).isEqualTo(productId1);
        assertThat(content.get(0).getName()).isEqualTo(productName1);
        assertThat(content.get(0).getMakerName()).isEqualTo(makerName);
        assertThat(content.get(0).getPrice()).isEqualTo(getProduct1().getDistributionPrice());
        assertThat(content.get(0).getQuantity()).isEqualTo(getProduct1().getQuantity());
        assertThat(content.get(0).getFile()).isEqualTo(null);

        assertThat(content.get(1).getId()).isEqualTo(productId2);
        assertThat(content.get(1).getName()).isEqualTo(productName2);
        assertThat(content.get(1).getMakerName()).isEqualTo(makerName);
        assertThat(content.get(1).getPrice()).isEqualTo(getProduct2().getDistributionPrice());
        assertThat(content.get(1).getQuantity()).isEqualTo(getProduct2().getQuantity());
        assertThat(content.get(1).getFile()).isEqualTo(null);
    }

    private Page<RestaurantOrder> getRestaurantOrders() {
        RestaurantOrder order = getRestaurantOrder();
        getRestaurantOrderDetail1().addOrder(order);
        getRestaurantOrderDetail2().addOrder(order);

        List<RestaurantOrder> list = List.of(order);
        Pageable pageable = PageRequest.of(page, size);

        return new PageImpl<>(list, pageable, list.size());
    }

    private List<RestaurantOrderRefund> getRestaurantOrderRefunds() {
        RestaurantOrderRefund refund = getRestaurantOrderRefund();
        getRestaurantOrderRefundDetail1().addOrderRefund(refund);
        getRestaurantOrderRefundDetail2().addOrderRefund(refund);

        return List.of(refund);
    }

    private Member getAdmin() {
        return Member.builder()
                .id(1L)
                .email("admin1@test.com")
                .provider(ProviderType.KAKAO)
                .name("admin1")
                .nickname("admin1")
                .role(MemberRole.ADMIN)
                .phone(1012345687L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(createdAt)
                .build();
    }

    private Member getOwner() {
        return Member.builder()
                .id(1L)
                .email("owner1@test.com")
                .provider(ProviderType.KAKAO)
                .name("owner1")
                .nickname("owner1")
                .role(MemberRole.OWNER)
                .phone(1012345687L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(createdAt)
                .build();
    }

    private Restaurant getRestaurant() {
        Member owner = getOwner();
        return Restaurant.builder()
                .id(restaurantId)
                .member(owner)
                .category("음식점")
                .name("레스쁘아")
                .address("서울특별시 종로구 종로8길 16")
                .location(Restaurant.genPoint(37.569343, 126.983857))
                .contact(212345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .businessName(businessName)
                .businessNumber("101-10-10001")
                .addressDetail("101")
                .postcode("00001")
                .createdAt(createdAt)
                .build();

    }

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
                .name(makerName)
                .address("강원도 횡성군 둔내면 강변로 975")
                .region("강원도 횡성군")
                .detail("101")
                .createdAt(createdAt)
                .build();
    }

    private Product getProduct1() {
        Maker maker = getMaker();
        Category category = getCategory();

        return Product.builder()
                .id(productId1)
                .name(productName1)
                .price(BigDecimal.valueOf(3500))
                .quantity(100L)
                .alcohol(5D)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(maker)
                .distributionPrice(BigDecimal.valueOf(3850.0))
                .category(category)
                .createdAt(createdAt)
                .build();
    }

    private Product getProduct2() {
        Maker maker = getMaker();
        Category category = getCategory();

        return Product.builder()
                .id(productId2)
                .name(productName2)
                .price(BigDecimal.valueOf(3200))
                .quantity(100L)
                .alcohol(5D)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(5L)
                .cool(5L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(maker)
                .distributionPrice(BigDecimal.valueOf(3520.0))
                .category(category)
                .createdAt(createdAt)
                .build();
    }

    private Page<Product> getProducts() {
        List<Product> list = List.of(this.getProduct1(), this.getProduct2());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Product>(list, pageable, list.size());
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
                .description(orderDescription)
                .postcode(orderPostcode)
                .recipient(owner.getName())
                .phone(owner.getPhone())
                .restaurant(restaurant)
                .member(restaurant.getMember())
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
