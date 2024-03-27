package com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.api;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.restaurant.dto.request.RestaurantStockModifyRequest;
import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
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
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import jakarta.persistence.EntityManager;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class AdminRestaurantStockControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    private final GeometryFactory geometryFactory = new GeometryFactory();

    @Autowired
    private RestaurantStockRepository restaurantStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private EntityManager em;

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

    private static final String OWNER = "owner1@test.com";
    private static final String ADMIN = "admin1@test.com";
    private static final String STORE_MANAGER = "storeManager1@test.com";

    @BeforeEach
    @Transactional
    void beforeEach() throws IOException {
        em.createNativeQuery("ALTER TABLE restaurant_stock AUTO_INCREMENT = 1").executeUpdate();

        Member owner = memberRepository.findByEmail(OWNER)
                .orElseGet(() -> memberRepository.save(Member.builder()
                        .email(OWNER)
                        .provider(ProviderType.KAKAO)
                        .name("owner1")
                        .nickname("owner1")
                        .role(MemberRole.OWNER)
                        .phone(1012345678L)
                        .certifyAt(null)
                        .agreedToServiceUse(true)
                        .agreedToServicePolicy(true)
                        .agreedToServicePolicyUse(true)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(null)
                        .deletedAt(null)
                        .build()));

        final Coordinate coordinate = new Coordinate(126.984634, 37.569833);
        Point restaurant_location = geometryFactory.createPoint(coordinate);
        Restaurant restaurant = Restaurant.builder()
                .member(owner)
                .category("한식")
                .name("맛있는 한식당")
                .address("서울시 강남구")
                .location(restaurant_location)
                .contact(1012345678L)
                .menu(getMenuTest())
                .time(getTimeTest())
                .provision(getProvisionTest())
                .createdAt(LocalDateTime.now())
                .build();

        restaurantRepository.save(restaurant);

        List<Product> products = LongStream.rangeClosed(1, 2).mapToObj(i -> {
            Product product = Product.builder()
                    .name("productName" + i)
                    .build();

            productRepository.save(product);

            MockMultipartFile multipartFile1 = JsonConvertor.getMockImg("files", "test1.txt", "test1 file");

            fileService.saveFiles(product, List.of(multipartFile1));

            return product;
        }).toList();

        List<RestaurantStock> restaurantStocks = products.stream().map(product ->
                RestaurantStock.builder()
                        .product(product)
                        .restaurant(restaurant)
                        .quantity(100L)
                        .createdAt(LocalDateTime.now())
                        .build()
        ).collect(Collectors.toList());

        restaurantStockRepository.saveAll(restaurantStocks);

        for (RestaurantStock restaurantStock : restaurantStocks) {
            System.out.println("restaurantStock = " + restaurantStock.getId());
        }
    }

    @AfterEach
    @Transactional
    void afterEach() {
        memberRepository.deleteAll();
        restaurantRepository.deleteAll();
        restaurantStockRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("레스토랑 재고 조회")
    @WithAccount(email = ADMIN, role = MemberRole.ADMIN)
    void getRestaurantStocksTest() throws Exception {
        // given
        Restaurant restaurant = this.restaurantRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/restaurants/" + restaurant.getId() + "/stocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminRestaurantStockController.class))
                .andExpect(handler().methodName("getRestaurantStocks"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].file", notNullValue()))
                .andExpect(jsonPath("$.data[1].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[1].name", notNullValue()))
                .andExpect(jsonPath("$.data[1].price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[1].quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[1].file", notNullValue()));
    }

    @Test
    @DisplayName("레스토랑 재고 수정")
    @WithAccount(email = ADMIN, role = MemberRole.ADMIN)
    void modifyRestaurantStockTest() throws Exception {
        // given
        Restaurant restaurant = this.restaurantRepository.findAll().get(0);

        RestaurantStockModifyRequest modifyRequest = RestaurantStockModifyRequest.builder()
                .id(1L)
                .price(BigDecimal.valueOf(20000))
                .quantity(50L)
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/restaurants/" + restaurant.getId() + "/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(modifyRequest)))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminRestaurantStockController.class))
                .andExpect(handler().methodName("modifyRestaurantStock"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)));
    }
}
