package com.drunkenlion.alcoholfriday.domain.item.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.ItemReviewResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.file.application.FileServiceImpl;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private FileServiceImpl fileService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ReviewRepository reviewRepository;

    // test를 위한 임의 변수
    private final Long itemId1 = 1L;
    private final String firstName = "식품";
    private final String lastName = "탁주/막걸리";
    private final String productName = "1000억 유산균막걸리";
    private final String itemName = "1000억 유산균막걸리 3개입";
    private final BigDecimal price = new BigDecimal(50000);
    private final String info = "이 상품은 테스트 상품입니다.";
    private final Long quantity = 10L;
    private final Double alcohol = 17.0D;
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
    private final String lastName2 = "약주/청주";
    private final String productName2 = "감자술 13도";
    private final String itemName2 = "감자술 13도 3개입";
    private final BigDecimal price2 = new BigDecimal(100_000);
    private final String info2 = "이 상품은 테스트 상품2 입니다.";
    private final Long quantity2 = 10L;
    private final Double alcohol2 = 17D;
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

    // Member
    private final Long id2 = 1L;
    private final String email2 = "test@example.com";
    private final String name2 = "테스트";
    private final String nickname2 = "test";
    private final Long phone2 = 1012345679L;

    // Order
    private final Long orderId = 1L;
    private final String orderNo = "240314-221628-987501-1";
    private final OrderStatus orderStatus = OrderStatus.ORDER_RECEIVED;
    private final BigDecimal deliveryPrice = BigDecimal.valueOf(2500);
    private final String recipient = "홍길동";
    private final String address = "서울특별시 중구 세종대로 110(태평로1가)";
    private final String addressDetail = "서울특별시청 103호";
    private final String description = "부재시 문앞에 놓아주세요.";
    private final String postcode = "04524";

    // OrderDetail
    private final Long orderDetailId = 1L;
    private final Long quantityItem = 2L;
    private final Long orderDetailId2 = 2L;
    private final Long quantityItem2 = 1L;

    // Review
    private final Long reviewId = 1L;
    private final Long reviewId2 = 1L;

    @Test
    void searchTest() {
        // given
        Mockito.when(this.itemRepository.search(any(), any(), any())).thenReturn(this.getSearch());

        List<String> categories = new ArrayList<>();
        categories.add("탁주/막걸리");

        List<Review> reviewList = new ArrayList<>();
        reviewList.add(getDataReview());
        reviewList.add(getDataReview2());
        Mockito.when((this.reviewRepository.findAllByItemIdAndDeletedAtIsNull(itemId1))).thenReturn(reviewList);

        // when
        Page<SearchItemResponse> search = this.itemService.search(0, 10, "1000억 유산균막걸리", categories);
        // then
        List<SearchItemResponse> content = search.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getName()).isEqualTo(itemName);
        assertThat(content.get(0).getPrice()).isEqualTo(price);
        assertThat(content.get(0).getCategory().getFirstName()).isEqualTo(firstName);
        assertThat(content.get(0).getCategory().getLastName()).isEqualTo(lastName);
        assertThat(content.get(0).getItemRating().getAvgItemScore()).isEqualTo(4.5);
        assertThat(content.get(0).getItemRating().getTotalReviewCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품 상세 조회")
    void getTest() {
        // given
        Mockito.when(this.itemRepository.get(any())).thenReturn(this.getOne());

        List<Review> reviewList = new ArrayList<>();
        reviewList.add(getDataReview());
        reviewList.add(getDataReview2());
        Mockito.when((this.reviewRepository.findAllByItemIdAndDeletedAtIsNull(itemId1))).thenReturn(reviewList);

        // when
        FindItemResponse findItemResponse = this.itemService.get(1L);
        // then
        assertThat(findItemResponse.getName()).isEqualTo(itemName);
        assertThat(findItemResponse.getPrice()).isEqualTo(price);
        assertThat(findItemResponse.getInfo()).isEqualTo(info);
        assertThat(findItemResponse.getProducts().isEmpty()).isFalse();
        assertThat(findItemResponse.getProducts().get(0).getName()).isEqualTo(productName);
        assertThat(findItemResponse.getProducts().get(0).getQuantity()).isEqualTo(quantity);
        assertThat(findItemResponse.getProducts().get(0).getAlcohol()).isEqualTo(alcohol);
        assertThat(findItemResponse.getProducts().get(0).getIngredient()).isEqualTo(ingredient);
        assertThat(findItemResponse.getProducts().get(0).getSweet()).isEqualTo(sweet);
        assertThat(findItemResponse.getProducts().get(0).getSour()).isEqualTo(sour);
        assertThat(findItemResponse.getProducts().get(0).getCool()).isEqualTo(cool);
        assertThat(findItemResponse.getProducts().get(0).getBody()).isEqualTo(body);
        assertThat(findItemResponse.getProducts().get(0).getBalance()).isEqualTo(balance);
        assertThat(findItemResponse.getProducts().get(0).getIncense()).isEqualTo(incense);
        assertThat(findItemResponse.getProducts().get(0).getThroat()).isEqualTo(throat);
        assertThat(findItemResponse.getCategory().getFirstName()).isEqualTo(firstName);
        assertThat(findItemResponse.getCategory().getLastName()).isEqualTo(lastName);
        assertThat(findItemResponse.getItemRating().getAvgItemScore()).isEqualTo(4.5);
        assertThat(findItemResponse.getItemRating().getTotalReviewCount()).isEqualTo(2);
    }

    @Test
    @DisplayName("상품 상세 조회 - 리뷰가 없는 경우")
    void getTest_noReview() {
        // given
        Mockito.when(this.itemRepository.get(any())).thenReturn(this.getOne());
        // when
        FindItemResponse findItemResponse = this.itemService.get(1L);
        // then
        assertThat(findItemResponse.getName()).isEqualTo(itemName);
        assertThat(findItemResponse.getPrice()).isEqualTo(price);
        assertThat(findItemResponse.getInfo()).isEqualTo(info);
        assertThat(findItemResponse.getProducts().isEmpty()).isFalse();
        assertThat(findItemResponse.getProducts().get(0).getName()).isEqualTo(productName);
        assertThat(findItemResponse.getProducts().get(0).getQuantity()).isEqualTo(quantity);
        assertThat(findItemResponse.getProducts().get(0).getAlcohol()).isEqualTo(alcohol);
        assertThat(findItemResponse.getProducts().get(0).getIngredient()).isEqualTo(ingredient);
        assertThat(findItemResponse.getProducts().get(0).getSweet()).isEqualTo(sweet);
        assertThat(findItemResponse.getProducts().get(0).getSour()).isEqualTo(sour);
        assertThat(findItemResponse.getProducts().get(0).getCool()).isEqualTo(cool);
        assertThat(findItemResponse.getProducts().get(0).getBody()).isEqualTo(body);
        assertThat(findItemResponse.getProducts().get(0).getBalance()).isEqualTo(balance);
        assertThat(findItemResponse.getProducts().get(0).getIncense()).isEqualTo(incense);
        assertThat(findItemResponse.getProducts().get(0).getThroat()).isEqualTo(throat);
        assertThat(findItemResponse.getCategory().getFirstName()).isEqualTo(firstName);
        assertThat(findItemResponse.getCategory().getLastName()).isEqualTo(lastName);
    }

    private Page<Item> getSearch() {
        List<Item> list = List.of(this.getData());
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<Item>(list, pageable, list.size());
    }

    private Optional<Item> getOne() {
        return Optional.of(this.getData());
    }

    private Item getData() {
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
                .balance(balance)
                .incense(incense)
                .throat(throat)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .id(itemId1)
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
                .balance(balance2)
                .incense(incense2)
                .throat(throat2)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .id(itemId2)
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

    private Optional<OrderDetail> getOneOrderDetail() {
        return Optional.of(this.getDataOrderDetail());
    }

    // Long 타입의 quantity를 BigDecimal로 변환
    BigDecimal quantityBigDecimal = BigDecimal.valueOf(quantityItem);
    // BigDecimal 타입의 price와 BigDecimal 타입의 quantity를 곱하기
    BigDecimal totalItemPrice = quantityBigDecimal.multiply(price);

    private OrderDetail getDataOrderDetail() {
        OrderDetail orderDetail = OrderDetail.builder()
                .id(orderDetailId)
                .itemPrice(price)
                .quantity(quantityItem)
                .totalPrice(totalItemPrice)
                .item(getData())
                .order(getDataOrder())
                .build();
        orderDetail.addItem(getData());
        orderDetail.addOrder(getDataOrder());

        return orderDetail;
    }

    private Optional<OrderDetail> getOneOrderDetail2() {
        return Optional.of(this.getDataOrderDetail2());
    }

    // Long 타입의 quantity를 BigDecimal로 변환
    BigDecimal quantityBigDecimal2 = BigDecimal.valueOf(quantityItem2);
    // BigDecimal 타입의 price와 BigDecimal 타입의 quantity를 곱하기
    BigDecimal totalItemPrice2 = quantityBigDecimal2.multiply(price2);

    private OrderDetail getDataOrderDetail2() {
        OrderDetail orderDetail = OrderDetail.builder()
                .id(orderDetailId2)
                .itemPrice(price2)
                .quantity(quantityItem2)
                .totalPrice(totalItemPrice2)
                .build();
        orderDetail.addItem(this.getDataItem2());
        orderDetail.addOrder(getDataOrder());

        return orderDetail;
    }

    private Optional<Order> getOneOrder() {
        return Optional.of(this.getDataOrder());
    }

    private Order getDataOrder() {
        Order order = Order.builder()
                .id(orderId)
                .orderNo(orderNo)
                .orderStatus(orderStatus)
                .deliveryPrice(deliveryPrice)
                .recipient(recipient)
                .phone(phone)
                .address(address)
                .addressDetail(addressDetail)
                .description(description)
                .postcode(postcode)
                .createdAt(LocalDateTime.now())
                .member(getDataMember())
                .build();

        return order;
    }

    private Optional<Member> getOneMember() {
        return Optional.of(this.getDataMember());
    }

    private Member getDataMember() {
        return Member.builder()
                .id(id)
                .email(email)
                .provider(ProviderType.byProviderName(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.byRole(role))
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

    private Optional<Member> getOneMember2() {
        return Optional.of(this.getDataMember2());
    }

    private Member getDataMember2() {
        return Member.builder()
                .id(id)
                .email(email2)
                .provider(ProviderType.byProviderName(provider))
                .name(name2)
                .nickname(nickname2)
                .role(MemberRole.byRole(role))
                .phone(phone2)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }

    private Optional<Review> getOneReview() {
        return Optional.of(this.getDataReview());
    }

    private Review getDataReview() {
        Review review = Review.builder()
                .id(reviewId)
                .score(5D)
                .content("맛있어요")
                .item(getData())
                .member(getDataMember())
                .build();
        review.addOrderDetail(getDataOrderDetail());

        return review;
    }

    private Optional<Review> getOneReview2() {
        return Optional.of(this.getDataReview2());
    }

    private Review getDataReview2() {
        Review review = Review.builder()
                .id(reviewId2)
                .score(4D)
                .content("맛있어요")
                .item(getData())
                .member(getDataMember2())
                .build();
        review.addOrderDetail(getDataOrderDetail());

        return review;
    }

    @Test
    @DisplayName("상품 리뷰 조회")
    public void t1() {
        Member member = Member.builder().id(1L).nickname("테스터").build();
        Item item = Item.builder().id(1L).build();

        Mockito.when(itemRepository.findById(1L)).thenReturn(
                Optional.ofNullable(item)
        );

        Review firstReview = Review.builder().id(1L).score(4.0d).content("리뷰 테스트").member(member).item(item).build();
        Review secondReview = Review.builder().id(2L).score(3.0d).content("리뷰 테스트").member(member).item(item).build();

        List<Review> reviews = List.of(firstReview, secondReview);
        Page<Review> reviewPage = new PageImpl<>(reviews);

        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(reviewRepository.findItemDetailReview(item,pageable)).thenReturn(
                reviewPage
        );

        assert item != null;
        Page<ItemReviewResponse> pageReviews = itemService.getReviews(item.getId(), 0, 10);
        List<ItemReviewResponse> reviewsResponse = pageReviews.getContent();
        ItemReviewResponse response = reviewsResponse.get(0);

        assertThat(reviewsResponse.size()).isEqualTo(reviews.size());
        assertThat(response.getId()).isEqualTo(firstReview.getId());
        assertThat(response.getScore()).isEqualTo(firstReview.getScore());
        assertThat(response.getContent()).isEqualTo(firstReview.getContent());
        assertThat(response.getNickname()).isEqualTo(member.getNickname());
    }
}