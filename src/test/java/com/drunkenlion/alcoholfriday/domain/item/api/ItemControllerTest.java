package com.drunkenlion.alcoholfriday.domain.item.api;

import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemProductRepository itemProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CategoryClassRepository categoryClassRepository;
    @Autowired
    private FileService fileService;

    @BeforeEach
    @Transactional
    void beforeEach() {
        CategoryClass 카테고리_대분류1 = CategoryClass.builder()
                .firstName("식품")
                .build();

        Category 카테고리_소분류1 = Category.builder()
                .lastName("탁주")
                .build();
        카테고리_소분류1.addCategoryClass(카테고리_대분류1);

        // Item1
        Product 제품_지란지교_탁주 = Product.builder()
                .name("지란지교 탁주")
                .quantity(10L)
                .alcohol(17L)
                .ingredient("알콜, 누룩 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_지란지교_탁주.addCategory(카테고리_소분류1);

        Item 상품_지란지교_탁주_1 = Item.builder()
                .name("지란지교 탁주 5개")
                .price(new BigDecimal(50000))
                .info("이 상품은 테스트 상품입니다.")
                .build();
        상품_지란지교_탁주_1.addCategory(카테고리_소분류1);

        ItemProduct 제품상세_지란지교 = ItemProduct.builder()
                .item(상품_지란지교_탁주_1)
                .product(제품_지란지교_탁주)
                .build();
        제품상세_지란지교.addItem(상품_지란지교_탁주_1);
        제품상세_지란지교.addProduct(제품_지란지교_탁주);

        categoryClassRepository.save(카테고리_대분류1);
        categoryRepository.save(카테고리_소분류1);
        productRepository.save(제품_지란지교_탁주);
        itemRepository.save(상품_지란지교_탁주_1);
        itemProductRepository.save(제품상세_지란지교);


        // Item2
        Product 제품_오늘_탁주 = Product.builder()
                .name("오늘 탁주")
                .quantity(10L)
                .alcohol(17L)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_오늘_탁주.addCategory(카테고리_소분류1);

        Item 상품_오늘_탁주_1 = Item.builder()
                .name("오늘 탁주 10개")
                .price(new BigDecimal(100000))
                .info("이 상품은 테스트 상품2입니다.")
                .build();
        상품_오늘_탁주_1.addCategory(카테고리_소분류1);

        ItemProduct 제품상세_오늘 = ItemProduct.builder()
                .item(상품_오늘_탁주_1)
                .product(제품_오늘_탁주)
                .build();
        제품상세_오늘.addItem(상품_오늘_탁주_1);
        제품상세_오늘.addProduct(제품_오늘_탁주);

        productRepository.save(제품_오늘_탁주);
        itemRepository.save(상품_오늘_탁주_1);
        itemProductRepository.save(제품상세_오늘);


        Category 카테고리_소분류2 = Category.builder()
                .lastName("와인")
                .build();
        카테고리_소분류2.addCategoryClass(카테고리_대분류1);

        // Item3
        Product 제품_villaM_와인 = Product.builder()
                .name("villa M 와인")
                .quantity(10L)
                .alcohol(17L)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_villaM_와인.addCategory(카테고리_소분류2);

        Item 상품_villaM_와인 = Item.builder()
                .name("villa M 와인 3개")
                .price(new BigDecimal(200000))
                .info("이 상품은 테스트 상품3입니다.")
                .build();
        상품_villaM_와인.addCategory(카테고리_소분류2);

        ItemProduct 제품상세_villaM = ItemProduct.builder()
                .item(상품_villaM_와인)
                .product(제품_villaM_와인)
                .build();
        제품상세_villaM.addItem(상품_villaM_와인);
        제품상세_villaM.addProduct(제품_villaM_와인);

        MockMultipartFile multipartFile1 = new MockMultipartFile("files", "test1.txt", "text/plain", "test1 file".getBytes(StandardCharsets.UTF_8));
        MockMultipartFile multipartFile2 = new MockMultipartFile("files", "test2.txt", "text/plain", "test2 file".getBytes(StandardCharsets.UTF_8));

        categoryRepository.save(카테고리_소분류2);
        productRepository.save(제품_villaM_와인);
        itemRepository.save(상품_villaM_와인);
        itemProductRepository.save(제품상세_villaM);
        fileService.saveFiles(상품_villaM_와인, List.of(multipartFile1, multipartFile2));

        // Item4
        Product 제품_피노누아_와인 = Product.builder()
                .name("피노 누아 와인")
                .quantity(10L)
                .alcohol(17L)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_피노누아_와인.addCategory(카테고리_소분류2);

        Item 상품_피노누아_와인 = Item.builder()
                .name("피노 누아 와인 3개")
                .price(new BigDecimal(200000))
                .info("이 상품은 테스트 상품4입니다.")
                .build();
        상품_피노누아_와인.addCategory(카테고리_소분류2);

        ItemProduct 제품상세_피노누아 = ItemProduct.builder()
                .item(상품_피노누아_와인)
                .product(제품_피노누아_와인)
                .build();
        제품상세_villaM.addItem(상품_피노누아_와인);
        제품상세_villaM.addProduct(제품_피노누아_와인);

        MockMultipartFile multipartFile3 = new MockMultipartFile("files", "test1.txt", "text/plain", "test1 file".getBytes(StandardCharsets.UTF_8));

        categoryRepository.save(카테고리_소분류2);
        productRepository.save(제품_피노누아_와인);
        itemRepository.save(상품_피노누아_와인);
        itemProductRepository.save(제품상세_피노누아);
        fileService.saveFiles(상품_피노누아_와인, List.of(multipartFile3));


        Category 카테고리_소분류3 = Category.builder()
                .lastName("소주")
                .build();
        카테고리_소분류3.addCategoryClass(카테고리_대분류1);

        // Item5
        Product 제품_참_소주 = Product.builder()
                .name("참 소주")
                .quantity(10L)
                .alcohol(17L)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_참_소주.addCategory(카테고리_소분류3);

        Item 상품_참_소주 = Item.builder()
                .name("참 소주 2개")
                .price(new BigDecimal(20000))
                .info("이 상품은 테스트 상품5입니다.")
                .build();
        상품_참_소주.addCategory(카테고리_소분류3);

        ItemProduct 제품상세_참 = ItemProduct.builder()
                .item(상품_참_소주)
                .product(제품_참_소주)
                .build();
        제품상세_참.addItem(상품_참_소주);
        제품상세_참.addProduct(제품_참_소주);

        categoryRepository.save(카테고리_소분류3);
        productRepository.save(제품_참_소주);
        itemRepository.save(상품_참_소주);
        itemProductRepository.save(제품상세_참);

        // Item6
        Product 제품_참이슬_소주 = Product.builder()
                .name("참이슬 소주")
                .quantity(10L)
                .alcohol(17L)
                .ingredient("알콜 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        제품_참이슬_소주.addCategory(카테고리_소분류3);

        Item 상품_참이슬_소주 = Item.builder()
                .name("참이슬 소주 3개")
                .price(new BigDecimal(20000))
                .info("이 상품은 테스트 상품6입니다.")
                .build();
        상품_참이슬_소주.addCategory(카테고리_소분류3);

        ItemProduct 제품상세_참이슬_소주 = ItemProduct.builder()
                .item(상품_참이슬_소주)
                .product(제품_참이슬_소주)
                .build();
        제품상세_참이슬_소주.addItem(상품_참이슬_소주);
        제품상세_참이슬_소주.addProduct(제품_참이슬_소주);

        MockMultipartFile multipartFile4 = new MockMultipartFile("files", "test1.txt", "text/plain", "test1 file".getBytes(StandardCharsets.UTF_8));

        categoryRepository.save(카테고리_소분류3);
        productRepository.save(제품_참이슬_소주);
        itemRepository.save(상품_참이슬_소주);
        itemProductRepository.save(제품상세_참이슬_소주);
        fileService.saveFiles(상품_참이슬_소주, List.of(multipartFile4));
    }

    @AfterEach
    @Transactional
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        categoryClassRepository.deleteAll();
    }

    @Test
    @DisplayName("item 검색")
    void searchTest() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "10")
                        .param("keywordType", "type,name")
                        .param("keyword", "탁주")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("이미지가 포함된 item 검색")
    void searchTest_img() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "10")
                        .param("keywordType", "type, name")
                        .param("keyword", "와인")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("이미지가 포함된 item 과 이미지가 없는 item 검색")
    void searchTest_img_notImg() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "10")
                        .param("keywordType", "type, name")
                        .param("keyword", "소주")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

//    @Test TODO 검색 기능 더 보충할 때 사용
    @DisplayName("키워드가 2개 이상")
    void searchTest_twoKeyword() throws Exception {
        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items")
                        .param("size", "10")
                        .param("keywordType", "type, name")
                        .param("keyword", "탁주 와인")
                )
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("search"))
                .andExpect(jsonPath("$.data", instanceOf(List.class)))
                .andExpect(jsonPath("$.data.length()", is(4)))
                .andExpect(jsonPath("$.data[0].id", notNullValue()))
                .andExpect(jsonPath("$.data[0].name", notNullValue()))
                .andExpect(jsonPath("$.data[0].price", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.firstName", notNullValue()))
                .andExpect(jsonPath("$.data[0].category.lastName", notNullValue()))
                .andExpect(jsonPath("$.pageInfo", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.pageInfo.size", notNullValue()))
                .andExpect(jsonPath("$.pageInfo.count", notNullValue()));
    }

    @Test
    @DisplayName("한 개 item 조회")
    void getTest() throws Exception {
        // given
        Item saved = this.itemRepository.findAll().get(0);

        // when
        ResultActions resultActions = mvc
                .perform(get("/v1/items/" + saved.getId()))
                .andDo(print());

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ItemController.class))
                .andExpect(handler().methodName("get"))
                .andExpect(jsonPath("$", instanceOf(LinkedHashMap.class)))
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", notNullValue()))
                .andExpect(jsonPath("$.price", notNullValue()))
                .andExpect(jsonPath("$.info", notNullValue()))
                .andExpect(jsonPath("$.category.firstName", notNullValue()))
                .andExpect(jsonPath("$.category.lastName", notNullValue()))
                .andExpect(jsonPath("$.products[0].name", notNullValue()))
                .andExpect(jsonPath("$.products[0].quantity", notNullValue()))
                .andExpect(jsonPath("$.products[0].alcohol", notNullValue()))
                .andExpect(jsonPath("$.products[0].ingredient", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sour", notNullValue()))
                .andExpect(jsonPath("$.products[0].cool", notNullValue()))
                .andExpect(jsonPath("$.products[0].body", notNullValue()))
                .andExpect(jsonPath("$.products[0].balance", notNullValue()))
                .andExpect(jsonPath("$.products[0].incense", notNullValue()))
                .andExpect(jsonPath("$.products[0].throat", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()))
                .andExpect(jsonPath("$.products[0].sweet", notNullValue()));
    }
}