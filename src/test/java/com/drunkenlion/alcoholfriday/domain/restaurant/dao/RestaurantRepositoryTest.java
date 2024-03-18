package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.application.RestaurantService;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantNearbyResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class RestaurantRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantStockRepository restaurantStockRepository;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private ProductRepository productRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final double neLatitude = 37.5567635;
    private final double neLongitude = 126.8529193;
    private final double swLatitude = 37.5482577;
    private final double swLongitude = 126.8421905;
    private final double restaurantLatitude = 37.549636;
    private final double restaurantLongitude = 126.842299;
    private final double userLocationLatitude = 37.552096;
    private final double userLocationLongitude = 126.845166;
    private final Integer page = 0;
    private final Integer size = 5;
    private final Double distance = 372.4291590383813;
    private final String restaurantName = "학식";
    private final String dongdongju = "동동주";
    private final String takju = "탁주";
    private final Long restaurantId = 1L;
    private final String restaurantCategory = "우정산 폴리텍대학";
    private final String restaurantAddress = "우정산 서울강서 캠퍼스";
    private final BigDecimal productPrice1 = BigDecimal.valueOf(30000);
    private final BigDecimal productPrice2 = BigDecimal.valueOf(20000);

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
                .price(productPrice1)
                .quantity(0L)
                .alcohol(100D)
                .build();

        Product product2 = Product.builder()
                .name(takju)
                .price(productPrice2)
                .quantity(0L)
                .alcohol(100D)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);

        final Coordinate coordinate = new Coordinate(126.842299, 37.549636);
        Point restaurant_location = geometryFactory.createPoint(coordinate);

        Restaurant restaurant = Restaurant.builder()
                .members(member)
                .category(restaurantCategory)
                .name(restaurantName)
                .address(restaurantAddress)
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
    @DisplayName("범위 내의 모든 레스토랑 정보 찾기")
    public void bounds() {
        //when
        List<RestaurantLocationResponse> restaurants = restaurantService.getRestaurants(neLatitude, neLongitude, swLatitude, swLongitude);

        //then
        assertThat(restaurants.get(0).getCategory()).isEqualTo(restaurantCategory);
        assertThat(restaurants.get(0).getName()).isEqualTo(restaurantName);
        assertThat(restaurants.get(0).getAddress()).isEqualTo(restaurantAddress);;
        assertThat(restaurants.get(0).getLatitude()).isEqualTo(restaurantLatitude);
        assertThat(restaurants.get(0).getLongitude()).isEqualTo(restaurantLongitude);
        assertThat(restaurants.get(0).getProductResponses().get(0).getPrice()).isEqualByComparingTo(productPrice2);
        assertThat(restaurants.get(0).getProductResponses().get(1).getPrice()).isEqualByComparingTo(productPrice1);
    }

    @Test
    @DisplayName("사용자의 위치로 부터 5km 이내의 가게 정보 조회")
    public void nearby() {
        //when
        Page<RestaurantNearbyResponse> restaurantNearbyResponses = restaurantService.get(userLocationLatitude, userLocationLongitude, dongdongju, page, size);

        List<RestaurantNearbyResponse> content = restaurantNearbyResponses.getContent();

        //then
        assertThat(content.get(0).getAddress()).isEqualTo(restaurantAddress);
        assertThat(content.get(0).getRestaurantName()).isEqualTo(restaurantName);
        assertThat(content.get(0).getProductName()).isEqualTo(dongdongju);
        assertThat(content.get(0).getDistance()).isEqualTo(distance);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        productRepository.deleteAll();
    }
}
