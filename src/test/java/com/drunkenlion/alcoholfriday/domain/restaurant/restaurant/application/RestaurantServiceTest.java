package com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.application.RestaurantServiceImpl;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantDetailProductResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantDetailResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantMapResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dto.response.RestaurantNearbyResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.domain.review.entity.Review;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @InjectMocks
    private RestaurantServiceImpl restaurantService;
    @Mock
    private FileService fileService;
    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantStockRepository restaurantStockRepository;

    @Mock
    private ItemRepository itemRepository;

    @AfterEach
    @Transactional
    public void after() {
        restaurantRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        itemRepository.deleteAll();
    }

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final double restaurantLatitude = 37.549636;
    private final double restaurantLongitude = 126.842299;
    private final Long restaurantId = 1L;
    private final Long memberId = 1L;
    private final Long productFirstId = 1L;
    private final Long productSecondId = 2L;
    private final BigDecimal productPrice1 = BigDecimal.valueOf(20000);
    private final BigDecimal productPrice2 = BigDecimal.valueOf(30000);
    private final Long quantitys = 50L;
    private final String restaurantName = "학식";
    private final String restaurantCategory = "우정산 폴리텍대학";
    private final String restaurantAddress = "우정산 서울강서 캠퍼스";
    private final String dongdongju = "동동주";
    private final String takju = "탁주";
    private final Long totalSize = 1L;

    @Test
    public void t1() {
        Page<RestaurantNearbyResponse> restaurant = this.getRestaurant();
        when(restaurantRepository.getRestaurantSellingProducts(anyDouble(), anyDouble(), any(),
                any(Pageable.class))).thenReturn(restaurant);

        Item item = Item.builder()
                .id(1L)
                .name("아이템")
                .build();

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        Page<RestaurantNearbyResponse> restaurantSellingProducts = restaurantService.findRestaurantWithItem(37.552250, 126.845024, 1L, 0, 5);

        RestaurantNearbyResponse firstResult = restaurantSellingProducts.getContent().get(0);
        assertEquals(restaurantId, firstResult.getRestaurantId());
        assertEquals(totalSize, restaurantSellingProducts.getTotalElements());
        assertEquals(restaurantName, firstResult.getRestaurantName());
        assertEquals(restaurantAddress, firstResult.getAddress());
        assertEquals(dongdongju, firstResult.getProductName());
    }

    @Test
    public void t2() {
        List<Restaurant> restaurants = this.of();

        when(restaurantRepository.getRestaurant(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(restaurants);

        List<RestaurantMapResponse> restaurantLocationResponses = restaurantService.findRestaurantInMap(anyDouble(), anyDouble(), anyDouble(), anyDouble());

        assertThat(restaurantLocationResponses.get(0).getRestaurantId()).isEqualTo(restaurantId);
    }

    @Test
    @DisplayName("가게 상세 조회")
    public void t3() {
        Restaurant restaurant = Restaurant.builder()
                .id(100L)
                .category(restaurantCategory)
                .name(restaurantName)
                .address(restaurantAddress)
                .contact(1012345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .createdAt(LocalDateTime.now())
                .build();

        when(restaurantRepository.findByIdAndDeletedAtIsNull(restaurant.getId())).thenReturn(
                Optional.of(restaurant)
        );

        RestaurantDetailResponse response = restaurantService.findRestaurant(restaurant.getId());

        assertThat(response.getRestaurantId()).isEqualTo(restaurant.getId());
        assertThat(response.getRestaurantName()).isEqualTo(restaurant.getName());
        assertThat(response.getRestaurantAddress()).isEqualTo(restaurant.getAddress());
    }

    @Test
    @DisplayName("가게 상세 조회")
    public void t4() {
        Restaurant restaurant = Restaurant.builder()
                .id(100L)
                .category(restaurantCategory)
                .name(restaurantName)
                .address(restaurantAddress)
                .contact(1012345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .createdAt(LocalDateTime.now())
                .build();

        Product product = Product.builder()
                .id(100L)
                .name("테스트")
                .quantity(10L)
                .build();

        RestaurantStock restaurantStock = RestaurantStock.builder()
                .id(100L)
                .product(product)
                .quantity(100L)
                .restaurant(restaurant)
                .build();

        List<RestaurantStock> restaurantStockList = List.of(restaurantStock);
        PageRequest pageable = PageRequest.of(0, 10);

        PageImpl<RestaurantStock> restaurantStocks = new PageImpl<>(restaurantStockList, pageable,
                restaurantStockList.size());

        when(restaurantStockRepository.findRestaurantStock(restaurant.getId(), pageable)).thenReturn(
                restaurantStocks
        );

        Page<RestaurantDetailProductResponse> responses = restaurantService.findRestaurantStock(
                restaurant.getId(), pageable.getPageNumber(), pageable.getPageSize());

        assertThat(responses.getContent().get(0).getName()).isEqualTo(product.getName());
        assertThat(responses.getContent().get(0).getStockStatus()).isEqualTo("재고있음");
    }

    private List<Restaurant> of() {
        return List.of(this.getRestaurantData());
    }

    private Page<RestaurantNearbyResponse> getRestaurant() {
        List<Restaurant> restaurantData = List.of(this.getRestaurantData());

        List<RestaurantNearbyResponse> restaurantNearbyResponses = List.of(
                RestaurantNearbyResponse.builder()
                .restaurantId(restaurantData.get(0).getId())
                .restaurantName(restaurantData.get(0).getName())
                .address(restaurantData.get(0).getAddress())
                .productName(restaurantData.get(0).getRestaurantStocks().get(0).getProduct().getName())
                .build());

        Pageable pageable = PageRequest.of(0, 5);

        return new PageImpl<>(restaurantNearbyResponses, pageable, restaurantNearbyResponses.size());
    }

    public Restaurant getRestaurantData() {

        Member memberData = Member.builder()
                .id(memberId)
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

        Coordinate coordinate = new Coordinate(restaurantLongitude, restaurantLatitude);
        Point restaurant_location = geometryFactory.createPoint(coordinate);

        Product product1 = Product.builder()
                .id(productFirstId)
                .name(dongdongju)
                .price(productPrice1)
                .quantity(quantitys)
                .alcohol(100D)
                .build();

        Product product2 = Product.builder()
                .id(productSecondId)
                .name(takju)
                .price(productPrice2)
                .quantity(quantitys)
                .alcohol(100D)
                .build();

        MockMultipartFile multipartFile1 = new MockMultipartFile("files", "test1.txt", "text/plain", "test1 file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("files", "test2.txt", "text/plain", "test2 file".getBytes(StandardCharsets.UTF_8));

        fileService.saveFiles(product1, List.of(multipartFile1, multipartFile2));
        fileService.saveFiles(product2, List.of(multipartFile1, multipartFile2));

        Restaurant restaurantData = Restaurant.builder()
                .id(restaurantId)
                .member(memberData)
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

        RestaurantStock stock1 = RestaurantStock.builder()
                .product(product1)
                .restaurant(restaurantData)
                .quantity(50L)
                .build();

        RestaurantStock stock2 = RestaurantStock.builder()
                .product(product2)
                .restaurant(restaurantData)
                .quantity(50L)
                .build();


        stock1.addRestaurant(restaurantData);
        stock2.addRestaurant(restaurantData);

        return restaurantData;

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

}