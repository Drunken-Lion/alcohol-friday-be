package com.drunkenlion.alcoholfriday.domain.item.api;

import com.drunkenlion.alcoholfriday.domain.address.dao.AddressRepository;
import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderDetailRepository;
import com.drunkenlion.alcoholfriday.domain.order.dao.OrderRepository;
import com.drunkenlion.alcoholfriday.domain.order.entity.Order;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.review.dao.ReviewRepository;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.common.enumerated.OrderStatus;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemProductRepository itemProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryClassRepository categoryClassRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private FileService fileService;

    public static final String EMAIL = "test@example.com";
    public static final String EMAIL2 = "test2@example.com";

    @BeforeEach
    @Transactional
    void beforeEach() {
        Member member = memberRepository.findByEmail(EMAIL)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(EMAIL)
                        .provider(ProviderType.KAKAO)
                        .name("테스트")
                        .nickname("test")
                        .role(MemberRole.MEMBER)
                        .phone(1012345678L)
                        .certifyAt(null)
                        .agreedToServiceUse(true)
                        .agreedToServicePolicy(true)
                        .agreedToServicePolicyUse(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .deletedAt(null)
                        .build()));

        Member member2 = memberRepository.findByEmail(EMAIL2)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(EMAIL2)
                        .provider(ProviderType.KAKAO)
                        .name("테스트2")
                        .nickname("test2")
                        .role(MemberRole.MEMBER)
                        .phone(1012345679L)
                        .certifyAt(null)
                        .agreedToServiceUse(true)
                        .agreedToServicePolicy(true)
                        .agreedToServicePolicyUse(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .deletedAt(null)
                        .build()));

        CategoryClass 카테고리_대분류1 = CategoryClass.builder()
                .firstName("식품")
                .build();

        Category 카테고리_소분류1 = Category.builder()
                .lastName("탁주/막걸리")
                .build();
        카테고리_소분류1.addCategoryClass(카테고리_대분류1);

        // Item1
        Product 제품_지란지교_탁주 = Product.builder()
                .name("지란지교 탁주")
                .quantity(10L)
                .alcohol(17.0D)
                .ingredient("알콜, 누룩 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_지란지교_탁주.addCategory(카테고리_소분류1);

        Item 상품_지란지교_탁주_1 = Item.builder()
                .name("지란지교 탁주 5개")
                .price(new BigDecimal(50000))
                .info("이 상품은 테스트 상품입니다.")
                .build();
        상품_지란지교_탁주_1.addCategory(카테고리_소분류1);

        ItemProduct 제품상세_지란지교 = ItemProduct.builder()
                .item(상품_지란지교_탁주_1)
                .product(제품_지란지교_탁주)
                .build();
        제품상세_지란지교.addItem(상품_지란지교_탁주_1);
        제품상세_지란지교.addProduct(제품_지란지교_탁주);

        categoryClassRepository.save(카테고리_대분류1);
        categoryRepository.save(카테고리_소분류1);
        productRepository.save(제품_지란지교_탁주);
        itemRepository.save(상품_지란지교_탁주_1);
        itemProductRepository.save(제품상세_지란지교);


        // Item2
        Product 제품_오늘_탁주 = Product.builder()
                .name("오늘 탁주")
                .quantity(10L)
                .alcohol(17D)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_오늘_탁주.addCategory(카테고리_소분류1);

        Item 상품_오늘_탁주_1 = Item.builder()
                .name("오늘 탁주 10개")
                .price(new BigDecimal(100000))
                .info("이 상품은 테스트 상품2입니다.")
                .build();
        상품_오늘_탁주_1.addCategory(카테고리_소분류1);

        ItemProduct 제품상세_오늘 = ItemProduct.builder()
                .item(상품_오늘_탁주_1)
                .product(제품_오늘_탁주)
                .build();
        제품상세_오늘.addItem(상품_오늘_탁주_1);
        제품상세_오늘.addProduct(제품_오늘_탁주);

        productRepository.save(제품_오늘_탁주);
        itemRepository.save(상품_오늘_탁주_1);
        itemProductRepository.save(제품상세_오늘);

        Order order =
                orderRepository.save(Order.builder()
                        .orderNo("240314-221628-987501-1")
                        .orderStatus(OrderStatus.PAYMENT_COMPLETED)
                        .price(BigDecimal.valueOf(20000L))
                        .deliveryPrice(BigDecimal.valueOf(2500L))
                        .totalPrice(BigDecimal.valueOf(23000L))
                        .recipient("테스트1")
                        .phone(1012345678L)
                        .address("서울특별시 마포구 연남동")
                        .addressDetail("123-12")
                        .description("부재시 연락주세요.")
                        .postcode("123123")
                        .member(member)
                        .build());

        OrderDetail orderDetail =
                orderDetailRepository.save(
                        OrderDetail.builder()
                                .itemPrice(상품_지란지교_탁주_1.getPrice())
                                .quantity(2L)
                                .totalPrice(BigDecimal.valueOf(100000))
                                .build());
        orderDetail.addItem(상품_지란지교_탁주_1);
        orderDetail.addOrder(order);

        OrderDetail orderDetail2 =
                orderDetailRepository.save(
                        OrderDetail.builder()
                                .itemPrice(상품_오늘_탁주_1.getPrice())
                                .quantity(1L)
                                .totalPrice(BigDecimal.valueOf(20000))
                                .build()
                );
        orderDetail2.addItem(상품_오늘_탁주_1);
        orderDetail2.addOrder(order);

        Address address = Address.builder()
                .member(member)
                .isPrimary(true)
                .address("서울시 마포구 연남동")
                .addressDetail("123-12번지")
                .postcode("123123")
                .recipient("테스트유저55")
                .phone(1012345678L)
                .request("부재시 문 앞")
                .build();
        addressRepository.save(address);

        Review review = reviewRepository.save(
                Review.builder()
                        .score(5D)
                        .content("맛있어요")
                        .item(상품_지란지교_탁주_1)
                        .member(member)
                        .build());
        review.addOrderDetail(orderDetail);

        Review review2 = reviewRepository.save(
                Review.builder()
                        .score(4D)
                        .content("맛있어요")
                        .item(상품_지란지교_탁주_1)
                        .member(member2)
                        .build());
        review.addOrderDetail(orderDetail);


        Category 카테고리_소분류2 = Category.builder()
                .lastName("과실주/와인")
                .build();
        카테고리_소분류2.addCategoryClass(카테고리_대분류1);

        // Item3
        Product 제품_villaM_와인 = Product.builder()
                .name("villa M 와인")
                .quantity(10L)
                .alcohol(17D)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_villaM_와인.addCategory(카테고리_소분류2);

        Item 상품_villaM_와인 = Item.builder()
                .name("villa M 와인 3개")
                .price(new BigDecimal(200000))
                .info("이 상품은 테스트 상품3입니다.")
                .build();
        상품_villaM_와인.addCategory(카테고리_소분류2);

        ItemProduct 제품상세_villaM = ItemProduct.builder()
                .item(상품_villaM_와인)
                .product(제품_villaM_와인)
                .build();
        제품상세_villaM.addItem(상품_villaM_와인);
        제품상세_villaM.addProduct(제품_villaM_와인);

        MockMultipartFile multipartFile1 = new MockMultipartFile("files", "test1.txt", "text/plain", "test1 file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("files", "test2.txt", "text/plain", "test2 file".getBytes(StandardCharsets.UTF_8));

        categoryRepository.save(카테고리_소분류2);
        productRepository.save(제품_villaM_와인);
        itemRepository.save(상품_villaM_와인);
        itemProductRepository.save(제품상세_villaM);
        fileService.saveFiles(상품_villaM_와인, List.of(multipartFile1, multipartFile2));

        // Item4
        Product 제품_피노누아_와인 = Product.builder()
                .name("피노 누아 와인")
                .quantity(10L)
                .alcohol(17D)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_피노누아_와인.addCategory(카테고리_소분류2);

        Item 상품_피노누아_와인 = Item.builder()
                .name("피노 누아 와인 3개")
                .price(new BigDecimal(200000))
                .info("이 상품은 테스트 상품4입니다.")
                .build();
        상품_피노누아_와인.addCategory(카테고리_소분류2);

        ItemProduct 제품상세_피노누아 = ItemProduct.builder()
                .item(상품_피노누아_와인)
                .product(제품_피노누아_와인)
                .build();
        제품상세_villaM.addItem(상품_피노누아_와인);
        제품상세_villaM.addProduct(제품_피노누아_와인);

        MockMultipartFile multipartFile3 = new MockMultipartFile("files", "test1.txt", "text/plain", "test1 file".getBytes(StandardCharsets.UTF_8));

        categoryRepository.save(카테고리_소분류2);
        productRepository.save(제품_피노누아_와인);
        itemRepository.save(상품_피노누아_와인);
        itemProductRepository.save(제품상세_피노누아);
        fileService.saveFiles(상품_피노누아_와인, List.of(multipartFile3));


        Category 카테고리_소분류3 = Category.builder()
                .lastName("증류주/소주/리큐르")
                .build();
        카테고리_소분류3.addCategoryClass(카테고리_대분류1);

        // Item5
        Product 제품_참_소주 = Product.builder()
                .name("참 소주")
                .quantity(10L)
                .alcohol(17D)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_참_소주.addCategory(카테고리_소분류3);

        Item 상품_참_소주 = Item.builder()
                .name("참 소주 2개")
                .price(new BigDecimal(20000))
                .info("이 상품은 테스트 상품5입니다.")
                .build();
        상품_참_소주.addCategory(카테고리_소분류3);

        ItemProduct 제품상세_참 = ItemProduct.builder()
                .item(상품_참_소주)
                .product(제품_참_소주)
                .build();
        제품상세_참.addItem(상품_참_소주);
        제품상세_참.addProduct(제품_참_소주);

        categoryRepository.save(카테고리_소분류3);
        productRepository.save(제품_참_소주);
        itemRepository.save(상품_참_소주);
        itemProductRepository.save(제품상세_참);

        // Item6
        Product 제품_참이슬_소주 = Product.builder()
                .name("참이슬 소주")
                .quantity(10L)
                .alcohol(17D)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_참이슬_소주.addCategory(카테고리_소분류3);

        Item 상품_참이슬_소주 = Item.builder()
                .name("참이슬 소주 3개")
                .price(new BigDecimal(20000))
                .info("이 상품은 테스트 상품6입니다.")
                .build();
        상품_참이슬_소주.addCategory(카테고리_소분류3);

        ItemProduct 제품상세_참이슬_소주 = ItemProduct.builder()
                .item(상품_참이슬_소주)
                .product(제품_참이슬_소주)
                .build();
        제품상세_참이슬_소주.addItem(상품_참이슬_소주);
        제품상세_참이슬_소주.addProduct(제품_참이슬_소주);

        MockMultipartFile multipartFile4 = new MockMultipartFile("files", "test1.txt", "text/plain", "test1 file".getBytes(StandardCharsets.UTF_8));

        categoryRepository.save(카테고리_소분류3);
        productRepository.save(제품_참이슬_소주);
        itemRepository.save(상품_참이슬_소주);
        itemProductRepository.save(제품상세_참이슬_소주);
        fileService.saveFiles(상품_참이슬_소주, List.of(multipartFile4));
    }

    @AfterEach
    @Transactional
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryClassRepository.deleteAll();
        memberRepository.deleteAll();
        orderRepository.deleteAll();
        orderDetailRepository.deleteAll();
        reviewRepository.deleteAll();
        addressRepository.deleteAll();
    }

    @Test
    @DisplayName("item 검색")
    void searchTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "12")
                        .param("categories", "탁주/막걸리")
                        .param("keyword", "탁주")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.data[0].itemRating.avgItemScore", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].itemRating.totalReviewCount", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[1].itemRating", nullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("이미지가 포함된 item 검색")
    void searchTest_img() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "12")
                        .param("categories", "과실주/와인")
                        .param("keyword", "와인")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("이미지가 포함된 item 과 이미지가 없는 item 검색")
    void searchTest_img_notImg() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "12")
                        .param("categories", "증류주/소주/리큐르")
                        .param("keyword", "소주")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

//    @Test TODO 검색 기능 더 보충할 때 사용
    @DisplayName("키워드가 2개 이상")
    void searchTest_twoKeyword() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "10")
                        .param("categories", "type, name")
                        .param("keyword", "탁주 와인")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(4)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("한 개 item 조회")
    void getTest() throws Exception {
        // given
        Item saved = this.itemRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items/" + saved.getId()))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("get"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.category.firstName", notNullValue()))
                .andExpect(jsonPath("$.category.lastName", notNullValue()))
                .andExpect(jsonPath("$.products[0].name", notNullValue()))
                .andExpect(jsonPath("$.products[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.products[0].alcohol", notNullValue()))
                .andExpect(jsonPath("$.products[0].ingredient", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sour", notNullValue()))
                .andExpect(jsonPath("$.products[0].cool", notNullValue()))
                .andExpect(jsonPath("$.products[0].body", notNullValue()))
                .andExpect(jsonPath("$.products[0].balance", notNullValue()))
                .andExpect(jsonPath("$.products[0].incense", notNullValue()))
                .andExpect(jsonPath("$.products[0].throat", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.itemRating.avgItemScore", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemRating.totalReviewCount", instanceOf(Number.class)));
    }

    @Test
    @DisplayName("한 개 item 조회 - 리뷰가 없는 경우")
    void getTest_noReview() throws Exception {
        // given
        Item saved = this.itemRepository.findAll().get(1);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items/" + saved.getId()))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("get"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.category.firstName", notNullValue()))
                .andExpect(jsonPath("$.category.lastName", notNullValue()))
                .andExpect(jsonPath("$.products[0].name", notNullValue()))
                .andExpect(jsonPath("$.products[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.products[0].alcohol", notNullValue()))
                .andExpect(jsonPath("$.products[0].ingredient", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sour", notNullValue()))
                .andExpect(jsonPath("$.products[0].cool", notNullValue()))
                .andExpect(jsonPath("$.products[0].body", notNullValue()))
                .andExpect(jsonPath("$.products[0].balance", notNullValue()))
                .andExpect(jsonPath("$.products[0].incense", notNullValue()))
                .andExpect(jsonPath("$.products[0].throat", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.itemRating", nullValue()));
    }

    @Test
    @DisplayName("상품 상세페이지의 리뷰는 누구나 확인이 가능하다.")
    public void t1() throws Exception {
        Member member = memberRepository.findByEmail("test@example.com").get();
        Item item = itemRepository.save(Item.builder().id(1L).build());

        reviewRepository.save(Review.builder().score(4.0d).content("리뷰 테스트").member(member).item(item).build());
        reviewRepository.save(Review.builder().score(3.0d).content("리뷰 테스트").member(member).item(item).build());
        
        ResultActions actions = mvc
                .perform(get("/v1/items/" + item.getId()+"/reviews"))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("getReview"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.data.[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data.[0].nickname", notNullValue()))
                .andExpect(jsonPath("$.data.[0].content", notNullValue()))
                .andExpect(jsonPath("$.data.[0].score", notNullValue()))
                .andExpect(jsonPath("$.data.[0].createdAt", notNullValue()))
        ;

    }
}