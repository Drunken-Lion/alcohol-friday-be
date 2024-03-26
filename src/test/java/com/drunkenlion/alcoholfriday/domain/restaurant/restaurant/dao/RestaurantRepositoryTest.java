package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao;

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
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.application.RestaurantService;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantMapResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantNearbyResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo.TimeData;
import java.time.LocalDate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemProductRepository itemProductRepository;
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

    private final Long itemId = 1L;
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
                .email("owner1@example.com")
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
                .member(member)
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

        Restaurant restaurant2 = Restaurant.builder()
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

        restaurantRepository.save(restaurant2);

        Member 회원_관리자 = memberRepository.save(Member.builder()
                .email("admin1@example.com")
                .provider(ProviderType.KAKAO)
                .name("관리자")
                .nickname("admin")
                .role(MemberRole.ADMIN)
                .phone(1041932693L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .build());

        final Coordinate 가게1_좌표 = new Coordinate(0, 0);
        final Point 가게1_위치 = geometryFactory.createPoint(가게1_좌표);

        Restaurant 가게1 = restaurantRepository.save(Restaurant.builder()// 1
                .member(회원_관리자)
                .category("퓨전 음식점")
                .name("원주")
                .address("서울특별시 종로구 종로8길 16")
                .location(가게1_위치) // 위도, 경도
                .contact(027331371L)
                .menu(Map.of("김치찌개", 8000, "순두부", 8000, "제육볶음", 8000, "황태국", 8000))
                .time(getTimeTest())
                .createdAt(LocalDateTime.now())
                .provision(getProvisionTest())
                .build());
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        productRepository.deleteAll();
        itemRepository.deleteAll();
        itemProductRepository.deleteAll();
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
    public void t1() {
        //when
        List<Restaurant> restaurants = restaurantRepository.getRestaurant(neLatitude, neLongitude, swLatitude,
                swLongitude);

        //then
        assertThat(restaurants.get(0).getCategory()).isEqualTo(restaurantCategory);
        assertThat(restaurants.get(0).getName()).isEqualTo(restaurantName);
        assertThat(restaurants.get(0).getAddress()).isEqualTo(restaurantAddress);
        assertThat(restaurants.get(0).getLocation().getX()).isEqualTo(restaurantLongitude);
        assertThat(restaurants.get(0).getLocation().getY()).isEqualTo(restaurantLatitude);
    }

    @Test
    @Transactional
    @DisplayName("사용자의 위치로 부터 5km 이내의 가게 정보 조회")
    public void t2() {
        //when
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

        Item item = itemRepository.save(Item.builder()
                .name("테스트")
                .info("테스트상품")
                .build());

        ItemProduct itemProduct = itemProductRepository.save(ItemProduct.builder()
                .product(product1)
                .build());

        itemProduct.addItem(item);
        itemProductRepository.save(itemProduct);
        itemRepository.save(item);

        PageRequest pageable = PageRequest.of(page, size);
        Page<RestaurantNearbyResponse> restaurantNearbyResponses =
                restaurantRepository.getRestaurantSellingProducts(userLocationLatitude, userLocationLongitude, item, pageable);

        List<RestaurantNearbyResponse> content = restaurantNearbyResponses.getContent();

        //then
        assertThat(content.get(0).getAddress()).isEqualTo(restaurantAddress);
        assertThat(content.get(0).getRestaurantName()).isEqualTo(restaurantName);
        assertThat(content.get(0).getProductName()).isEqualTo(dongdongju);
        assertThat(content.get(0).getDistanceKm()).isNotNull();
    }

    @Test
    @DisplayName("전체 매장 조회 - ADMIN")
    void findAllBasedAuthAdminTest() {
        // given
        Member 회원_관리자 = memberRepository.findByEmail("admin1@example.com").get();
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Restaurant> search = restaurantRepository.findAllBasedAuth(회원_관리자, pageable);

        // then
        assertThat(search.getContent()).isInstanceOf(List.class);
        assertThat(search.getContent().size()).isEqualTo(3);
    }

    @Test
    @DisplayName("전체 매장 조회 - OWNER")
    void findAllBasedAuthOwnerTest() {
        // given
        Member 회원_사장1 = memberRepository.findByEmail("owner1@example.com").get();
        Pageable pageable = PageRequest.of(0, 20);

        // when
        Page<Restaurant> search = restaurantRepository.findAllBasedAuth(회원_사장1, pageable);

        // then
        assertThat(search.getContent()).isInstanceOf(List.class);
        assertThat(search.getContent().size()).isEqualTo(2);
    }
}
