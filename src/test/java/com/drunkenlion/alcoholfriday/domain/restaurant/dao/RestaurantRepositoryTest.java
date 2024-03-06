package com.drunkenlion.alcoholfriday.domain.restaurant.dao;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.dao.MemberRepository;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.restaurant.application.RestaurantService;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
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
    private ItemRepository itemRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    private double neLatitude = 37.5567635;
    private double neLongitude = 126.8529193;
    private double swLatitude = 37.5482577;
    private double swLongitude = 126.8421905;
    private double restaurantLatitude = 37.549636;
    private double restaurantLongitude = 126.842299;
    private String restaurantName = "학식";
    private String restaurantCategory = "우정산 폴리텍대학";
    private String restaurantAddress = "우정산 서울강서 캠퍼스";

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

        Item item1 = Item.builder()
                .type(ItemType.PROMOTION)
                .name("item 1")
                .price(BigDecimal.valueOf(20000))
                .info("item 1 info")
                .build();
        Item item2 = Item.builder()
                .type(ItemType.PROMOTION)
                .name("item 2")
                .price(BigDecimal.valueOf(30000))
                .info("item 2 info")
                .build();

        itemRepository.save(item1);
        itemRepository.save(item2);

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
                .item(item1)
                .restaurant(restaurant)
                .quantity(100L)
                .build();

        RestaurantStock stock2 = RestaurantStock.builder()
                .item(item2)
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
    public void nearbyRestaurant() {

        //when
        List<RestaurantLocationResponse> restaurants = restaurantService.getRestaurants(neLatitude, neLongitude, swLatitude, swLongitude);

        System.out.println("==============================================================");
        for (RestaurantLocationResponse restaurant : restaurants) {
            System.out.println(restaurant);
        }
        System.out.println("==============================================================");

        //then
        assertThat(restaurants.get(0).getCategory()).isEqualTo(restaurantCategory);
        assertThat(restaurants.get(0).getName()).isEqualTo(restaurantName);
        assertThat(restaurants.get(0).getAddress()).isEqualTo(restaurantAddress);;
        assertThat(restaurants.get(0).getLatitude()).isEqualTo(restaurantLatitude);
        assertThat(restaurants.get(0).getLongitude()).isEqualTo(restaurantLongitude);
        assertThat(restaurants.get(0).getStockResponses().get(0).getItem().getPrice()).isEqualTo(BigDecimal.valueOf(20000));
        assertThat(restaurants.get(0).getStockResponses().get(1).getItem().getPrice()).isEqualTo(BigDecimal.valueOf(30000));

    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        itemRepository.deleteAll();
    }
}
