package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.api;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
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
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
public class RestaurantControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private ItemProductRepository itemProductRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RestaurantStockRepository restaurantStockRepository;

    @Autowired
    private ProductRepository productRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    private double neLatitude = 37.5567635;
    private double neLongitude = 126.8529193;
    private double swLatitude = 37.5482577;
    private double swLongitude = 126.8421905;
    private final String dongdongju = "동동주";
    private final String takju = "탁주";


    @BeforeEach
    @Transactional
    void beforeEach() {
        Member member = Member.builder()
                .email("toss1@example.com")
                .provider(ProviderType.KAKAO)
                .name("toss_1")
                .nickname("toss_1")
                .role(MemberRole.MEMBER)
                .phone(1012345678L)
                .certifyAt(null)
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .deletedAt(null)
                .build();

        memberRepository.save(member);

        Product product1 = Product.builder()
                .name(dongdongju)
                .price(BigDecimal.valueOf(0L))
                .quantity(0L)
                .alcohol(100D)
                .build();

        Product product2 = Product.builder()
                .name(takju)
                .price(BigDecimal.valueOf(0L))
                .quantity(0L)
                .alcohol(100D)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        final Coordinate coordinate = new Coordinate(126.842299, 37.549636);
        Point restaurant_location = geometryFactory.createPoint(coordinate);

        Restaurant restaurant = Restaurant.builder()
                .member(member)
                .category("학식")
                .name("우정산 폴리텍대학")
                .address("우정산 서울강서 캠퍼스")
                .location(restaurant_location)
                .contact(1012345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .createdAt(LocalDateTime.now())
                .build();

        restaurantRepository.save(restaurant);

        RestaurantStock stock1 = RestaurantStock.builder()
                .product(product1)
                .restaurant(restaurant)
                .quantity(100L)
                .build();

        RestaurantStock stock2 = RestaurantStock.builder()
                .product(product2)
                .restaurant(restaurant)
                .quantity(150L)
                .build();

        stock1.addRestaurant(restaurant);
        stock2.addRestaurant(restaurant);

        restaurantStockRepository.save(stock1);
        restaurantStockRepository.save(stock2);

        Item item = Item.builder()
                .name("테스트")
                .type(ItemType.REGULAR)
                .price(BigDecimal.valueOf(100000))
                .info("테스트 상품")
                .build();
        itemRepository.save(item);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .quantity(100L)
                .build();
        itemProductRepository.save(itemProduct);
        itemProduct.addItem(item);
        itemProduct.addProduct(product1);

        itemRepository.save(item);
        itemProductRepository.save(itemProduct);
        productRepository.save(product1);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        productRepository.deleteAll();
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private Map<String, Object> getMenuTest() {
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put("비빔밥", 8000);
        frame.put("돌솥 비빔밥", 5000);
        frame.put("불고기", 12000);
        frame.put("백반", 8000);
        return frame;
    }

    private Map<String, Object> getTimeTest() {
        Map<String, Object> allDayTime = new LinkedHashMap<>();

        allDayTime.put(TimeOption.HOLIDAY.toString(), true);
        allDayTime.put(TimeOption.ETC.toString(), "여름/겨울 방학 휴업");

        TimeData timeData = TimeData.builder()
                .businessStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .breakBusinessStatus(true)
                .breakStartTime(LocalTime.of(15, 0))
                .breakEndTime(LocalTime.of(17, 0))
                .build();

        TimeData result = null;

        for (DayInfo value : DayInfo.values()) {
            allDayTime.put(value.toString(), timeData);
            result = (TimeData) allDayTime.get(value);
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

    @Test
    @DisplayName("상품에 포함된 제품을 사용자 주변 5km 내 취급 매장 조회")
    public void t1() throws Exception {
        Product product1 = Product.builder()
                .name(dongdongju)
                .price(BigDecimal.valueOf(0L))
                .quantity(0L)
                .alcohol(100D)
                .build();
        productRepository.save(product1);

        Item item = Item.builder()
                .name("테스트")
                .type(ItemType.REGULAR)
                .price(BigDecimal.valueOf(100000))
                .info("테스트 상품")
                .build();
        itemRepository.save(item);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .quantity(100L)
                .build();
        itemProductRepository.save(itemProduct);
        itemProduct.addItem(item);
        itemProduct.addProduct(product1);

        itemRepository.save(item);
        itemProductRepository.save(itemProduct);
        productRepository.save(product1);

        ResultActions actions = mvc
                .perform(get("/v1/restaurants/nearby")
                        .param("userLocationLatitude", "37.552250")
                        .param("userLocationLongitude", "126.845024")
                        .param("itemId", String.valueOf(item.getId()))
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantController.class))
                .andExpect(handler().methodName("getRestaurantsWithinNearby"))
                .andExpect(jsonPath("$.data[0].restaurantId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].distanceKm", notNullValue()))
                .andExpect(jsonPath("$.data[0].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].restaurantName", notNullValue()))
                .andExpect(jsonPath("$.data[0].address", notNullValue()))
        ;
    }

    @Test
    @DisplayName("사용자 화면 내 매장 조회")
    public void t2() throws Exception {
        ResultActions actions = mvc.perform(get("/v1/restaurants")
                        .param("neLatitude", String.valueOf(neLatitude))
                        .param("neLongitude", String.valueOf(neLongitude))
                        .param("swLatitude", String.valueOf(swLatitude))
                        .param("swLongitude", String.valueOf(swLongitude)))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantController.class))
                .andExpect(handler().methodName("getRestaurantsWithinBounds"))
                .andExpect(jsonPath("$.[0].restaurantId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.[0].latitude", notNullValue()))
                .andExpect(jsonPath("$.[0].longitude", notNullValue()))
                .andExpect(jsonPath("$.[0].restaurantName", notNullValue()))
                .andExpect(jsonPath("$.[0].restaurantCategory", notNullValue()))
                .andExpect(jsonPath("$.[0].restaurantAddress", notNullValue()))
                .andExpect(jsonPath("$.[0].businessStatus", notNullValue()))
                .andExpect(jsonPath("$.[0].provision", notNullValue()))
                .andExpect(jsonPath("$.[0].restaurantProducts", notNullValue()))
        ;
    }

    @Test
    @DisplayName("매장 상세 조회")
    public void t3() throws Exception {
        Restaurant restaurant = Restaurant.builder()
                .category("학식")
                .name("우정산 폴리텍대학")
                .address("우정산 서울강서 캠퍼스")
                .contact(1012345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .createdAt(LocalDateTime.now())
                .build();

        restaurantRepository.save(restaurant);


        ResultActions actions = mvc.perform(get("/v1/restaurants/" + restaurant.getId()))
                .andDo(print());

        actions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantController.class))
                .andExpect(handler().methodName("getRestaurant"))
                .andExpect(jsonPath("$.restaurantId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.restaurantName", notNullValue()))
                .andExpect(jsonPath("$.restaurantMenu", notNullValue()))
                .andExpect(jsonPath("$.restaurantAddress", notNullValue()))
                .andExpect(jsonPath("$.businessStatus", notNullValue()))
                .andExpect(jsonPath("$.restaurantContactNumber", notNullValue()))
                .andExpect(jsonPath("$.provision", notNullValue()))
                .andExpect(jsonPath("$.businessTime", notNullValue()))
        ;
    }

}