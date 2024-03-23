package com.drunkenlion.alcoholfriday.domain.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.dto.response.RestaurantLocationResponse;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @InjectMocks
    private RestaurantServiceImpl restaurantService;
    @Mock
    private FileService fileService;
    @Mock
    private RestaurantRepository restaurantRepository;

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

    @Test
    public void get() {
        List<Restaurant> restaurants = this.of();

        when(restaurantRepository.getRestaurant(anyDouble(), anyDouble(), anyDouble(), anyDouble())).thenReturn(restaurants);

        List<RestaurantLocationResponse> restaurantLocationResponses = restaurantService.getRestaurants(anyDouble(), anyDouble(), anyDouble(), anyDouble());

        assertThat(restaurantLocationResponses.get(0).getId()).isEqualTo(restaurantId);
        assertThat(restaurantLocationResponses.get(0).getMemberId()).isEqualTo(memberId);
        assertThat(restaurantLocationResponses.get(0).getCategory()).isEqualTo(restaurantCategory);
        assertThat(restaurantLocationResponses.get(0).getName()).isEqualTo(restaurantName);
        assertThat(restaurantLocationResponses.get(0).getAddress()).isEqualTo(restaurantAddress);
        assertThat(restaurantLocationResponses.get(0).getLatitude()).isEqualTo(restaurantLatitude);
        assertThat(restaurantLocationResponses.get(0).getLongitude()).isEqualTo(restaurantLongitude);
        assertThat(restaurantLocationResponses.get(0).getProductResponses().get(0).getId()).isEqualTo(productFirstId);
        assertThat(restaurantLocationResponses.get(0).getProductResponses().get(1).getId()).isEqualTo(productSecondId);
        assertThat(restaurantLocationResponses.get(0).getProductResponses().get(0).getName()).isEqualTo(dongdongju);
        assertThat(restaurantLocationResponses.get(0).getProductResponses().get(1).getName()).isEqualTo(takju);
        assertThat(restaurantLocationResponses.get(0).getProductResponses().get(0).getPrice()).isEqualByComparingTo(productPrice1);
        assertThat(restaurantLocationResponses.get(0).getProductResponses().get(1).getPrice()).isEqualByComparingTo(productPrice2);
        assertThat(restaurantLocationResponses.get(0).getProductResponses().get(0).getQuantity()).isEqualTo(quantitys);
    }

    private List<Restaurant> of() {
        return List.of(this.getRestaurantData());
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
