package com.drunkenlion.alcoholfriday.domain.admin.product.api;

import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductCreateRequest;
import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductModifyRequest;
import com.drunkenlion.alcoholfriday.domain.admin.product.dto.ProductQuantityRequest;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
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
public class AdminProductControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MakerRepository makerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryClassRepository categoryClassRepository;

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
                        .build()
        );

        Product 제품_국순당_프리바이오 = productRepository.save(Product.builder()
                .name("1000억 막걸리 프리바이오")
                .price(BigDecimal.valueOf(10000))
                .distributionPrice(BigDecimal.valueOf(15000))
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

        MockMultipartFile multipartFile1 = JsonConvertor.getMockImg("files", "test1.txt", "test1 file");
        MockMultipartFile multipartFile2 = JsonConvertor.getMockImg("files", "test1.txt", "test1 file");

        fileService.saveFiles(제품_국순당_프리바이오, List.of(multipartFile1, multipartFile2));
    }

    @AfterEach
    @Transactional
    void afterEach() {
        makerRepository.deleteAll();
        categoryClassRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    @DisplayName("제품 목록 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getProductsTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/products")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("getProducts"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(1)))
                .andExpect(jsonPath("$.data[0].id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.data[0].categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].makerName", notNullValue()))
                .andExpect(jsonPath("$.data[0].createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.data[0].deleted", instanceOf(Boolean.class)))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("제품 상세 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void getProductTest() throws Exception {
        // given
        Product product = this.productRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("getProduct"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.distributionPrice", instanceOf(Number.class)))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.alcohol", instanceOf(Number.class)))
                .andExpect(jsonPath("$.ingredient", notNullValue()))
                .andExpect(jsonPath("$.sweet", instanceOf(Number.class)))
                .andExpect(jsonPath("$.sour", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cool", instanceOf(Number.class)))
                .andExpect(jsonPath("$.body", instanceOf(Number.class)))
                .andExpect(jsonPath("$.balance", instanceOf(Number.class)))
                .andExpect(jsonPath("$.incense", instanceOf(Number.class)))
                .andExpect(jsonPath("$.throat", instanceOf(Number.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.productFiles", notNullValue()));
    }

    @Test
    @DisplayName("제품 등록 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void createProductTest() throws Exception {
        // given
        MockMultipartFile multipartFile1 = JsonConvertor.getMockImg("files", "create-test1.txt", "create-test1 file");

        Long categoryLastId = this.categoryRepository.findAll().get(0).getId();
        Long makerId = this.makerRepository.findAll().get(0).getId();

        ProductCreateRequest productCreateRequest = ProductCreateRequest.builder()
                .categoryLastId(categoryLastId)
                .name("test 제품명")
                .makerId(makerId)
                .price(BigDecimal.valueOf(10000))
                .distributionPrice(BigDecimal.valueOf(15000))
                .alcohol(0.0D)
                .ingredient("test 쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(0L)
                .sour(0L)
                .cool(0L)
                .body(0L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .build();

        MockMultipartFile productRequest = JsonConvertor.mockBuild(productCreateRequest, "productRequest");

        // when
        ResultActions resultActions = mvc
                .perform(multipart("/v1/admin/products")
                        .file(multipartFile1)
                        .file(productRequest)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isCreated())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("createProduct"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.distributionPrice", instanceOf(Number.class)))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.alcohol", instanceOf(Number.class)))
                .andExpect(jsonPath("$.ingredient", notNullValue()))
                .andExpect(jsonPath("$.sweet", instanceOf(Number.class)))
                .andExpect(jsonPath("$.sour", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cool", instanceOf(Number.class)))
                .andExpect(jsonPath("$.body", instanceOf(Number.class)))
                .andExpect(jsonPath("$.balance", instanceOf(Number.class)))
                .andExpect(jsonPath("$.incense", instanceOf(Number.class)))
                .andExpect(jsonPath("$.throat", instanceOf(Number.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.productFiles", notNullValue()));
    }

    @Test
    @DisplayName("제품 수정 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void modifyProductTest() throws Exception {
        // given
        MockMultipartFile multipartFile1 = JsonConvertor.getMockImg("files", "modify-test1.txt", "modify-test1 file");

        Product product = this.productRepository.findAll().get(0);
        Long categoryLastId = this.categoryRepository.findAll().get(0).getId();
        Long makerId = this.makerRepository.findAll().get(0).getId();

        ProductModifyRequest productModifyRequest = ProductModifyRequest.builder()
                .categoryLastId(categoryLastId)
                .name("test 제품명")
                .makerId(makerId)
                .price(BigDecimal.valueOf(10000))
                .distributionPrice(BigDecimal.valueOf(15000))
                .alcohol(0.0D)
                .ingredient("test 쌀(국내산), 밀(국내산), 누룩, 정제수")
                .sweet(0L)
                .sour(0L)
                .cool(0L)
                .body(0L)
                .balance(0L)
                .incense(0L)
                .throat(0L)
                .remove(List.of(1))
                .build();

        MockMultipartFile productRequest = JsonConvertor.mockBuild(productModifyRequest, "productRequest");

        MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart("/v1/admin/products/" + product.getId());
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
                        .file(productRequest)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("modifyProduct"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryLastId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.categoryFirstName", notNullValue()))
                .andExpect(jsonPath("$.categoryLastName", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.makerId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.makerName", notNullValue()))
                .andExpect(jsonPath("$.price", instanceOf(Number.class)))
                .andExpect(jsonPath("$.distributionPrice", instanceOf(Number.class)))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)))
                .andExpect(jsonPath("$.alcohol", instanceOf(Number.class)))
                .andExpect(jsonPath("$.ingredient", notNullValue()))
                .andExpect(jsonPath("$.sweet", instanceOf(Number.class)))
                .andExpect(jsonPath("$.sour", instanceOf(Number.class)))
                .andExpect(jsonPath("$.cool", instanceOf(Number.class)))
                .andExpect(jsonPath("$.body", instanceOf(Number.class)))
                .andExpect(jsonPath("$.balance", instanceOf(Number.class)))
                .andExpect(jsonPath("$.incense", instanceOf(Number.class)))
                .andExpect(jsonPath("$.throat", instanceOf(Number.class)))
                .andExpect(jsonPath("$.createdAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.updatedAt", matchesPattern(TestUtil.DATETIME_PATTERN)))
                .andExpect(jsonPath("$.deletedAt", anyOf(is(matchesPattern(TestUtil.DATETIME_PATTERN)), is(nullValue()))))
                .andExpect(jsonPath("$.productFiles", notNullValue()));
    }

    @Test
    @DisplayName("제품 삭제 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void deleteProductTest() throws Exception {
        // given
        Product product = this.productRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(delete("/v1/admin/products/" + product.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isNoContent())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("deleteProduct"));
    }

    @Test
    @DisplayName("재고 수량 조회 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void t6() throws Exception {
        // given
        Product product = this.productRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/admin/products/" + product.getId() + "/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("getQuantity"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)));
    }

    @Test
    @DisplayName("재고 수량 수정 성공")
    @WithAccount(role = MemberRole.ADMIN)
    void t7() throws Exception {
        // given
        Product product = this.productRepository.findAll().get(0);

        ProductQuantityRequest request = ProductQuantityRequest.builder()
                .quantity(10L)
                .build();

        // when
        ResultActions resultActions = mvc
                .perform(put("/v1/admin/products/" + product.getId() + "/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertor.build(request))
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminProductController.class))
                .andExpect(handler().methodName("modifyQuantity"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.quantity", instanceOf(Number.class)));
    }
}
