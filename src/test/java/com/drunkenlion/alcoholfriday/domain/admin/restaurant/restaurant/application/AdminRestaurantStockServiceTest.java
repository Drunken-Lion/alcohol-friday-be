package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.application;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.response.RestaurantStockListResponse;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.dao.RestaurantStockRepository;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.RestaurantStock;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.DayInfo;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.Provision;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.enumerated.TimeOption;
import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.vo.TimeData;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminRestaurantStockServiceTest {
    @InjectMocks
    private AdminRestaurantStockServiceImpl adminRestaurantStockService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private RestaurantStockRepository restaurantStockRepository;

    @Mock
    private FileService fileService;

    // Product
    private Long productId1 = 1L;
    private String productName1 = "1000억 막걸리 프리바이오";
    private Long productId2 = 2L;
    private String productName2 = "1000억 유산균막걸리";
    private String makerName = "(주)국순당";

    // RestaurantStock
    private Long stockId1 = 1L;
    private Long stockId2 = 2L;
    private Long stockQuantity = 100L;
    private BigDecimal stockPrice = BigDecimal.valueOf(20000);

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final int page = 0;
    private final int size = 20;

    @Test
    @DisplayName("매장 재고 조회")
    public void getRestaurantStocksTest() {
        // given
        when(restaurantRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.of(getRestaurant()));

        when(restaurantStockRepository.findRestaurantStocks(any(), any(), any(Pageable.class)))
                .thenReturn(getRestaurantStockPages());

        when(fileService.findOne(any())).thenReturn(null);

        // when
        Page<RestaurantStockListResponse> stocks =
                adminRestaurantStockService.getRestaurantStocks(getAdmin(), getRestaurant().getId(), page, size);

        // then
        List<RestaurantStockListResponse> content = stocks.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(2);
        assertThat(content.get(0).getId()).isEqualTo(stockId1);
        assertThat(content.get(0).getName()).isEqualTo(productName1);
        assertThat(content.get(0).getPrice()).isEqualTo(stockPrice);
        assertThat(content.get(0).getQuantity()).isEqualTo(stockQuantity);
        assertThat(content.get(0).getFile()).isEqualTo(null);
        assertThat(content.get(1).getId()).isEqualTo(stockId2);
        assertThat(content.get(1).getName()).isEqualTo(productName2);
        assertThat(content.get(1).getPrice()).isEqualTo(stockPrice);
        assertThat(content.get(1).getQuantity()).isEqualTo(stockQuantity);
        assertThat(content.get(1).getFile()).isEqualTo(null);
    }

    private Page<RestaurantStock> getRestaurantStockPages() {
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<>(getRestaurantStocks(), pageable, getRestaurantStocks().size());
    }

    private Member getAdmin() {
        return Member.builder()
                .id(1L)
                .email("admin1@test.com")
                .provider(ProviderType.KAKAO)
                .name("admin1")
                .nickname("admin1")
                .role(MemberRole.ADMIN)
                .phone(1012345678L)
                .certifyAt(LocalDate.now())
                .agreedToServiceUse(true)
                .agreedToServicePolicy(true)
                .agreedToServicePolicyUse(true)
                .createdAt(createdAt)
                .build();
    }

    private Restaurant getRestaurant() {
        Member owner = getAdmin();

        return Restaurant.builder()
                .id(1L)
                .member(owner)
                .category("음식점")
                .name("레스쁘아")
                .address("서울특별시 종로구 종로8길 16")
                .location(Restaurant.genPoint(37.569343, 126.983857))
                .contact(212345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .businessName("레스쁘아")
                .businessNumber("101-10-10001")
                .addressDetail("101")
                .postcode("00001")
                .createdAt(createdAt)
                .build();
    }

    private Map<String, Object> getMenuTest() {
        Map<String, Object> frame = new LinkedHashMap<>();
        frame.put("비빔밥", 8000);
        frame.put("불고기", 12000);
        return frame;
    }

    private Map<String, Object> getTimeTest() {
        Map<String, Object> allDayTime = new LinkedHashMap<>();

        allDayTime.put(TimeOption.HOLIDAY.toString(), true);
        allDayTime.put(TimeOption.ETC.toString(), "명절 당일만 휴업");

        TimeData timeData = TimeData.builder()
                .businessStatus(true)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(22, 0))
                .breakBusinessStatus(true)
                .breakStartTime(LocalTime.of(15, 0))
                .breakEndTime(LocalTime.of(17, 0))
                .build();

        for (DayInfo value : DayInfo.values()) {
            allDayTime.put(value.toString(), timeData);
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

    private CategoryClass getCategoryClass() {
        return CategoryClass.builder()
                .id(1L)
                .firstName("전통주")
                .createdAt(createdAt)
                .build();
    }

    private Category getCategory() {
        CategoryClass categoryClass = getCategoryClass();
        return Category.builder()
                .id(1L)
                .categoryClass(categoryClass).lastName("탁주/막걸리")
                .createdAt(createdAt)
                .build();
    }

    private Maker getMaker() {
        return Maker.builder()
                .id(1L)
                .name(makerName)
                .address("강원도 횡성군 둔내면 강변로 975")
                .region("강원도 횡성군")
                .detail("101")
                .createdAt(createdAt)
                .build();
    }

    private Product getProduct1() {
        Maker maker = getMaker();
        Category category = getCategory();

        return Product.builder()
                .id(productId1)
                .name(productName1)
                .price(BigDecimal.valueOf(3500))
                .quantity(100L)
                .alcohol(5D)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(maker)
                .distributionPrice(BigDecimal.valueOf(3850.0))
                .category(category)
                .createdAt(createdAt)
                .build();
    }

    private Product getProduct2() {
        Maker maker = getMaker();
        Category category = getCategory();

        return Product.builder()
                .id(productId2)
                .name(productName2)
                .price(BigDecimal.valueOf(3200))
                .quantity(100L)
                .alcohol(5D)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(5L)
                .cool(5L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(maker)
                .distributionPrice(BigDecimal.valueOf(3520.0))
                .category(category)
                .createdAt(createdAt)
                .build();
    }

    private RestaurantStock getRestaurantStock1() {
        return RestaurantStock.builder()
                .id(stockId1)
                .product(getProduct1())
                .restaurant(getRestaurant())
                .quantity(stockQuantity)
                .price(stockPrice)
                .build();
    }

    private RestaurantStock getRestaurantStock2() {
        return RestaurantStock.builder()
                .id(stockId2)
                .product(getProduct2())
                .restaurant(getRestaurant())
                .quantity(stockQuantity)
                .price(stockPrice)
                .build();
    }

    private List<RestaurantStock> getRestaurantStocks() {
        List<RestaurantStock> stocks = new ArrayList<>();
        stocks.add(getRestaurantStock1());
        stocks.add(getRestaurantStock2());

        return stocks;
    }
}
