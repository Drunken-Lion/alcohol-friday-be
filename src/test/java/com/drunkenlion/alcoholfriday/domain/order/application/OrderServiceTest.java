package com.drunkenlion.alcoholfriday.domain.order.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderItemRequest;
import com.drunkenlion.alcoholfriday.domain.order.dto.request.OrderRequestList;
import com.drunkenlion.alcoholfriday.domain.order.dto.response.OrderResponseList;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
class OrderServiceTest {
    @InjectMocks
    private OrderServiceImpl orderService;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderDetailRepository orderDetailRepository;
    @Mock
    private ItemRepository itemRepository;

    // test를 위한 임의 변수
    // Item
    private final Long itemId1 = 1L;
    private final String firstName = "식품";
    private final String lastName = "탁주";
    private final String productName = "test data";
    private final String itemName = "test ddaattaa";
    private final BigDecimal price = new BigDecimal(50000);
    private final String info = "이 상품은 테스트 상품입니다.";
    private final Long quantity = 10L;
    private final Long alcohol = 17L;
    private final String ingredient = "알콜, 누룩 등등...";
    private final Long sweet = 10L;
    private final Long sour = 10L;
    private final Long cool = 10L;
    private final Long body = 10L;
    private final Long balance = 10L;
    private final Long incense = 10L;
    private final Long throat = 10L;

    // Item2
    private final Long itemId2 = 2L;
    private final String firstName2 = "식품";
    private final String lastName2 = "청주";
    private final String productName2 = "test data2";
    private final String itemName2 = "test ddaattaa";
    private final BigDecimal price2 = new BigDecimal(100_000);
    private final String info2 = "이 상품은 테스트 상품2 입니다.";
    private final Long quantity2 = 10L;
    private final Long alcohol2 = 17L;
    private final String ingredient2 = "알콜, 누룩 등등...";
    private final Long sweet2 = 10L;
    private final Long sour2 = 10L;
    private final Long cool2 = 10L;
    private final Long body2 = 10L;
    private final Long balance2 = 10L;
    private final Long incense2 = 10L;
    private final Long throat2 = 10L;

    // Member
    private final Long id = 1L;
    private final String email = "test@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String name = "테스트";
    private final String nickname = "test";
    private final String role = MemberRole.MEMBER.getRole();
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = null;
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = null;
    private final LocalDateTime deletedAt = null;
    private final int page = 0;
    private final int size = 20;

    // Order
    private String orderNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "__" + 1;
    private OrderStatus orderStatus = OrderStatus.ORDER_RECEIVED;
    private String recipient = "홍길동";
    private String address = "서울특별시 중구 세종대로 110(태평로1가)";
    private String detail = "서울특별시청 103호";
    private String description = "부재시 문앞에 놓아주세요.";
    private Long postcode = 04524L;

    // OrderDetail
    private Long quantityItem = 2L;
    private Long quantityItem2 = 1L;


    // 바로 주문
    @Test
    @DisplayName("상품 한 개 주문할 경우")
    void orderReceive_oneItem() {
        // given
        // orderRepository.save(order)
        when(orderRepository.save(any(Order.class))).thenReturn(this.getDataOrder());

        // itemRepository.findById(orderItemRequest.getItemId())
        when(itemRepository.findById(itemId1)).thenReturn(this.getOneItem());

        // orderDetailRepository.save(orderDetail)
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(this.getDataOrderDetail());

        List<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder()
                .itemId(itemId1)
                .quantity(quantityItem)
                .build();
        orderItemRequestList.add(orderItemRequest);


        OrderRequestList orderRequestList = OrderRequestList.builder()
                .orderItemList(orderItemRequestList)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .detail(detail)
                .description(description)
                .postcode(postcode)
                .build();


        // when
        OrderResponseList receive = this.orderService.receive(orderRequestList, getDataMember());

        // then
        assertThat(receive.getRecipient()).isEqualTo(recipient);
        assertThat(receive.getOrderStatus()).isEqualTo(orderStatus);
        assertThat(receive.getOrderNo()).isEqualTo(orderNo);
        assertThat(receive.getItemList().get(0).getItem().getPrice()).isEqualTo("50000");
        assertThat(receive.getTotalPrice()).isEqualTo(new BigDecimal("100000"));
        assertThat(receive.getTotalQuantity()).isEqualTo(2L);
    }

    @Test
    @DisplayName("상품 한 개 이상 주문할 경우")
    void orderReceive_itemList() {
        // given
        // orderRepository.save(order)
        when(orderRepository.save(any(Order.class))).thenReturn(this.getDataOrder());

        // itemRepository.findById(orderItemRequest.getItemId())
        when(itemRepository.findById(itemId1)).thenReturn(this.getOneItem());
        when(itemRepository.findById(itemId2)).thenReturn(this.getOneItem2());

        // orderDetailRepository.save(orderDetail)
        when(orderDetailRepository.save(any(OrderDetail.class))).thenReturn(this.getDataOrderDetail()).thenReturn(this.getDataOrderDetail2());

        List<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder()
                .itemId(itemId1)
                .quantity(quantityItem)
                .build();
        orderItemRequestList.add(orderItemRequest);
        OrderItemRequest orderItemRequest2 = OrderItemRequest.builder()
                .itemId(itemId2)
                .quantity(quantityItem2)
                .build();
        orderItemRequestList.add(orderItemRequest2);

        OrderRequestList orderRequestList = OrderRequestList.builder()
                .orderItemList(orderItemRequestList)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .detail(detail)
                .description(description)
                .postcode(postcode)
                .build();

        // when
        OrderResponseList receive = this.orderService.receive(orderRequestList, getDataMember());

        // then
        assertThat(receive.getRecipient()).isEqualTo(recipient);
        assertThat(receive.getOrderStatus()).isEqualTo(orderStatus);
        assertThat(receive.getOrderNo()).isEqualTo(orderNo);
        assertThat(receive.getItemList().get(0).getItem().getPrice()).isEqualTo("50000");
        assertThat(receive.getItemList().get(1).getItem().getPrice()).isEqualTo("100000");
        assertThat(receive.getTotalPrice()).isEqualTo(new BigDecimal("200000"));
        assertThat(receive.getTotalQuantity()).isEqualTo(3L);
    }

    @Test
    @DisplayName("없는 상품 주문할 경우")
    void orderReceive_noItem() {
        // given
        // orderRepository.save(order)
        when(orderRepository.save(any(Order.class))).thenReturn(this.getDataOrder());

        // itemRepository.findById(orderItemRequest.getItemId())
        when(itemRepository.findById(100L)).thenReturn(Optional.empty());

        List<OrderItemRequest> orderItemRequestList = new ArrayList<>();
        OrderItemRequest orderItemRequest = OrderItemRequest.builder()
                .itemId(100L)
                .quantity(quantityItem)
                .build();
        orderItemRequestList.add(orderItemRequest);
        OrderItemRequest orderItemRequest2 = OrderItemRequest.builder()
                .itemId(itemId2)
                .quantity(quantityItem2)
                .build();
        orderItemRequestList.add(orderItemRequest2);

        OrderRequestList orderRequestList = OrderRequestList.builder()
                .orderItemList(orderItemRequestList)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .detail(detail)
                .description(description)
                .postcode(postcode)
                .build();

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            orderService.receive(orderRequestList, getDataMember());
        });
    }


    private Optional<OrderDetail> getOneOrderDetail() {
        return Optional.of(this.getDataOrderDetail());
    }

    // Long 타입의 quantity를 BigDecimal로 변환
    BigDecimal quantityBigDecimal = BigDecimal.valueOf(quantityItem);
    // BigDecimal 타입의 price와 BigDecimal 타입의 quantity를 곱하기
    BigDecimal totalItemPrice = quantityBigDecimal.multiply(price);

    private OrderDetail getDataOrderDetail() {
        return OrderDetail.builder()
                .itemPrice(price)
                .quantity(quantityItem)
                .totalPrice(totalItemPrice)
                .item(getDataItem())
                .order(getDataOrder())
                .build();
    }

    private Optional<OrderDetail> getOneOrderDetail2() {
        return Optional.of(this.getDataOrderDetail2());
    }

    // Long 타입의 quantity를 BigDecimal로 변환
    BigDecimal quantityBigDecimal2 = BigDecimal.valueOf(quantityItem2);
    // BigDecimal 타입의 price와 BigDecimal 타입의 quantity를 곱하기
    BigDecimal totalItemPrice2 = quantityBigDecimal2.multiply(price2);

    private OrderDetail getDataOrderDetail2() {
        return OrderDetail.builder()
                .itemPrice(price2)
                .quantity(quantityItem2)
                .totalPrice(totalItemPrice2)
                .item(getDataItem2())
                .order(getDataOrder())
                .build();
    }

    private Optional<Order> getOneOrder() {
        return Optional.of(this.getDataOrder());
    }

    private Order getDataOrder() {
        return Order.builder()
                .id(1L)
                .orderNo(orderNo)
                .orderStatus(orderStatus)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .detail(detail)
                .description(description)
                .postcode(postcode)
                .member(getDataMember())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Optional<Member> getOneMember() {
        return Optional.of(this.getDataMember());
    }

    private Member getDataMember() {
        return Member.builder()
                .id(id)
                .email(email)
                .provider(ProviderType.ofProvider(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.ofRole(role))
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    private Optional<Item> getOneItem() {
        return Optional.of(this.getDataItem());
    }

    private Item getDataItem() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName)
                .build();

        Category category = Category.builder()
                .lastName(lastName)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName)
                .quantity(quantity)
                .alcohol(alcohol)
                .ingredient(ingredient)
                .sweet(sweet)
                .sour(sour)
                .cool(cool)
                .body(body)
                .balence(balance)
                .insense(incense)
                .throat(throat)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name(itemName)
                .price(price)
                .info(info)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }

    private Optional<Item> getOneItem2() {
        return Optional.of(this.getDataItem2());
    }

    private Item getDataItem2() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName2)
                .build();

        Category category = Category.builder()
                .lastName(lastName2)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName2)
                .quantity(quantity2)
                .alcohol(alcohol2)
                .ingredient(ingredient2)
                .sweet(sweet2)
                .sour(sour2)
                .cool(cool2)
                .body(body2)
                .balence(balance2)
                .insense(incense2)
                .throat(throat2)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name(itemName2)
                .price(price2)
                .info(info2)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }
}