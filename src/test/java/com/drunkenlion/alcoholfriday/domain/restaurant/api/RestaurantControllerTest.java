package com.drunkenlion.alcoholfriday.domain.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
                .members(member)
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
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        productRepository.deleteAll();
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
    @DisplayName("사용자 위치로 부터 내의 모든 레스토랑 정보 조회")
    public void nearby() throws Exception {
        ResultActions nearby = mvc
                .perform(get("/v1/restaurants/nearby")
                        .param("userLocationLatitude", "37.552250")
                        .param("userLocationLongitude", "126.845024")
                        .param("keyword", "동동주")
                        .param("page", "0")
                        .param("size", "5"))
                .andDo(print());
        nearby
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantController.class))
                .andExpect(handler().methodName("getRestaurantsWithinNearby"))
                .andExpect(jsonPath("$.data[0].restaurantId", notNullValue()))
                .andExpect(jsonPath("$.data[0].restaurantName", notNullValue()))
                .andExpect(jsonPath("$.data[0].address", notNullValue()))
                .andExpect(jsonPath("$.data[0].productName", notNullValue()))
                .andExpect(jsonPath("$.data[0].distance", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("polygon 영역 내의 모든 레스토랑 정보 조회")
    public void bounds() throws Exception {

        ResultActions bounds = mvc.perform(get("/v1/restaurants")
                        .param("neLatitude", String.valueOf(neLatitude))
                        .param("neLongitude", String.valueOf(neLongitude))
                        .param("swLatitude", String.valueOf(swLatitude))
                        .param("swLongitude", String.valueOf(swLongitude)))
                .andDo(print());

        bounds
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(RestaurantController.class))
                .andExpect(handler().methodName("getRestaurantsWithinBounds"))
                .andExpect(jsonPath("$.[0].id", notNullValue()))
                .andExpect(jsonPath("$.[0].memberId", notNullValue()))
                .andExpect(jsonPath("$.[0].category", notNullValue()))
                .andExpect(jsonPath("$.[0].name", notNullValue()))
                .andExpect(jsonPath("$.[0].address", notNullValue()))
                .andExpect(jsonPath("$.[0].latitude", notNullValue()))
                .andExpect(jsonPath("$.[0].longitude", notNullValue()))
                .andExpect(jsonPath("$.[0].contact", notNullValue()))
                .andExpect(jsonPath("$.[0].menu", notNullValue()))
                .andExpect(jsonPath("$.[0].time", notNullValue()))
                .andExpect(jsonPath("$.[0].productResponses[0].name", notNullValue()))
                .andExpect(jsonPath("$.[0].productResponses[1].name", notNullValue()))
                .andExpect(jsonPath("$.[0].productResponses[0].price", notNullValue()))
                .andExpect(jsonPath("$.[0].productResponses[1].price", notNullValue()))
                .andExpect(jsonPath("$.[0].productResponses[0].alcohol", notNullValue()))
                .andExpect(jsonPath("$.[0].productResponses[1].alcohol", notNullValue()))
                .andExpect(jsonPath("$.[0].productResponses[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.[0].productResponses[1].quantity", notNullValue()));
    }

}
