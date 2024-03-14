package com.drunkenlion.alcoholfriday.domain.admin.item.api;

import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemProductInfo;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import com.drunkenlion.alcoholfriday.global.common.util.JsonConvertor;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.user.WithAccount;
import com.drunkenlion.alcoholfriday.global.util.TestUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class AdminItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CategoryClassRepository categoryClassRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ItemProductRepository itemProductRepository;

    @Autowired
    private FileService fileService;

    @BeforeEach
    @Transactional
    void beforeEach() {
        Maker 제조사_국순당 = makerRepository.save( // 1
                Maker.builder()
                        .name("(주)국순당")
                        .address("강원도 횡성군 둔내면 강변로 975")
                        .detail("101")
                        .region("강원도")
                        .build());

        CategoryClass 카테고리_대분류1 = categoryClassRepository.save(
                CategoryClass.builder()
                        .firstName("테스트 카테고리 대분류 1")
                        .build());

        Category 카테고리_소분류1 = categoryRepository.save(
                Category.builder()
                        .lastName("테스트 카테고리 소분류1")
                        .categoryClass(카테고리_대분류1)
                        .build());


        Product 제품_국순당_프리바이오 = productRepository.save(Product.builder()
                .name("1000억 막걸리 프리바이오")
                .price(BigDecimal.valueOf(10000))
                .quantity(100L)
                .alcohol(5.0D)
                .ingredient("쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(3L)
                .sour(4L)
                .cool(3L)
                .body(3L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .maker(제조사_국순당)
                .category(카테고리_소분류1)
                .build());

        Item 상품_1 = itemRepository.save(
                Item.builder()
                        .type(ItemType.REGULAR)
                        .name("프리바이오 막걸리 10개")
                        .price(BigDecimal.valueOf(20000))
                        .info("국순당 프리바이오 막걸리 10개입")
                        .category(카테고리_소분류1)
                        .build());

        MockMultipartFile multipartFile1 = JsonConvertor.getMockImg("files", "test1.txt", "test1 file");
        MockMultipartFile multipartFile2 = JsonConvertor.getMockImg("files", "test1.txt", "test1 file");

        fileService.saveFiles(상품_1, List.of(multipartFile1, multipartFile2));

        ItemProduct 상품상세1 = itemProductRepository.save(
                ItemProduct.builder()
                        .item(상품_1)
                        .product(제품_국순당_프리바이오)
                        .quantity(100L)
                        .build());
    }

    @AfterEach
    @Transactional
    void afterEach() {
        makerRepository.deleteAll();
        categoryClassRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        itemRepository.deleteAll();
        itemProductRepository.deleteAll();
    }

    @Test
    @DisplayName("상품 목록 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getItemsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/items")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminItemController.class))
                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getItemTest() throws Exception {
        // given
        Item item = this.itemRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/items/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminItemController.class))
                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemProductInfos", notNullValue()))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.itemFiles", notNullValue()));
    }

    @Test
    @DisplayName("상품 등록 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void createItemTest() throws Exception {
        // given
        MockMultipartFile multipartFile1 = JsonConvertor.getMockImg("files", "create-test1.txt", "create-test1 file");

        Long productId = this.productRepository.findAll().get(0).getId();
        Long categoryLastId = this.categoryRepository.findAll().get(0).getId();

        ItemCreateRequest itemCreateRequest =  ItemCreateRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(productId)
                                .quantity(0L)
                                .build()
                ))
                .categoryLastId(categoryLastId)
                .name("test 제품명")
                .price(BigDecimal.valueOf(100000))
                .info("test 정보")
                .type(ItemType.PROMOTION)
                .build();

        MockMultipartFile itemRequest = JsonConvertor.mockBuild(itemCreateRequest, "itemRequest");

        // when
        ResultActions resultActions = mvc
                .perform(multipart("/v1/admin/items")
                        .file(multipartFile1)
                        .file(itemRequest)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminItemController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemProductInfos", notNullValue()))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.itemFiles", notNullValue()));
    }

    @Test
    @DisplayName("상품 수정 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void modifyItemTest() throws Exception {
        // given
        MockMultipartFile multipartFile1 = JsonConvertor.getMockImg("files", "modify-test1.txt", "modify-test1 file");

        Item item = this.itemRepository.findAll().get(0);
        Long productId = this.productRepository.findAll().get(0).getId();
        Long categoryLastId = this.categoryRepository.findAll().get(0).getId();

        ItemModifyRequest itemModifyRequest =  ItemModifyRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(productId)
                                .quantity(0L)
                                .build()
                ))
                .categoryLastId(categoryLastId)
                .name("test 제품명 수정")
                .price(BigDecimal.valueOf(2000))
                .info("test 정보 수정")
                .type(ItemType.PROMOTION)
                .remove(List.of(1))
                .build();

        MockMultipartFile itemRequest = JsonConvertor.mockBuild(itemModifyRequest, "itemRequest");

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/v1/admin/items/" + item.getId());
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        // when
        ResultActions resultActions = mvc
                .perform(builder
                        .file(multipartFile1)
                        .file(itemRequest)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminItemController.class))
                .andExpect(handler().methodName("modifyItem"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.itemProductInfos", notNullValue()))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.type", notNullValue()))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.itemFiles", notNullValue()));
    }

    @Test
    @DisplayName("상품 삭제 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void deleteItemTest() throws Exception {
        // given
        Item item = this.itemRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/items/" + item.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminItemController.class))
                .andExpect(handler().methodName("deleteItem"));
    }
}
