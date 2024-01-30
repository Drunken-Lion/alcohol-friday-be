package com.drunkenlion.alcoholfriday.domain.item.application;

import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemRequest;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemProductRepository itemProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    @Transactional
    void beforeEach() {
        Category category = Category.builder()
                .firstName("식품")
                .middleName("전통주")
                .lastName("탁주")
                .build();

        Product product = Product.builder()
                .name("test data")
                .quantity(10L)
                .alcohol(17L)
                .ingredient("알콜, 누룩 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balence(1L)
                .insense(1L)
                .throat(1L)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name("test ddaattaa")
                .price(new BigDecimal(50000))
                .info("이 상품은 테스트 상품입니다.")
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        categoryRepository.save(category);
        productRepository.save(product);
        itemRepository.save(item);
        itemProductRepository.save(itemProduct);
    }

    @AfterEach
    @Transactional
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void searchTest() {
        // given
        List<String> list = new ArrayList<>();
        list.add("type");
        list.add("name");

        SearchItemRequest searchItemRequest = SearchItemRequest.builder()
                .size(10)
                .keywordType(list)
                .keyword("탁주")
                .build();
        // when
        Page<SearchItemResponse> search = this.itemService.search(searchItemRequest);
        // then
        List<SearchItemResponse> content = search.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isNotNull();
        assertThat(content.get(0).getName()).isEqualTo("test ddaattaa");
        assertThat(content.get(0).getPrice()).isEqualTo(new BigDecimal(50000));
        assertThat(content.get(0).getInfo()).isEqualTo("이 상품은 테스트 상품입니다.");
        assertThat(content.get(0).getCategory().getFirstName()).isEqualTo("식품");
        assertThat(content.get(0).getCategory().getMiddleName()).isEqualTo("전통주");
        assertThat(content.get(0).getCategory().getLastName()).isEqualTo("탁주");
    }

    @Test
    void getTest() {
        // given
        Item saved = this.itemRepository.findAll().get(0);
        // when
        FindItemResponse findItemResponse = this.itemService.get(saved.getId());
        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getItemProducts().isEmpty()).isFalse();
        assertThat(findItemResponse.getId()).isEqualTo(saved.getId());
        assertThat(findItemResponse.getName()).isEqualTo(saved.getName());
        assertThat(findItemResponse.getPrice()).isEqualTo(saved.getPrice());
        assertThat(findItemResponse.getInfo()).isEqualTo(saved.getInfo());
        assertThat(findItemResponse.getProducts().isEmpty()).isFalse();
        assertThat(findItemResponse.getProducts().get(0).getName()).isEqualTo(saved.getItemProducts().get(0).getProduct().getName());
        assertThat(findItemResponse.getProducts().get(0).getQuantity()).isEqualTo(saved.getItemProducts().get(0).getProduct().getQuantity());
        assertThat(findItemResponse.getProducts().get(0).getAlcohol()).isEqualTo(saved.getItemProducts().get(0).getProduct().getAlcohol());
        assertThat(findItemResponse.getProducts().get(0).getIngredient()).isEqualTo(saved.getItemProducts().get(0).getProduct().getIngredient());
        assertThat(findItemResponse.getProducts().get(0).getSweet()).isEqualTo(saved.getItemProducts().get(0).getProduct().getSweet());
        assertThat(findItemResponse.getProducts().get(0).getSour()).isEqualTo(saved.getItemProducts().get(0).getProduct().getSour());
        assertThat(findItemResponse.getProducts().get(0).getCool()).isEqualTo(saved.getItemProducts().get(0).getProduct().getCool());
        assertThat(findItemResponse.getProducts().get(0).getBody()).isEqualTo(saved.getItemProducts().get(0).getProduct().getBody());
        assertThat(findItemResponse.getProducts().get(0).getBalence()).isEqualTo(saved.getItemProducts().get(0).getProduct().getBalence());
        assertThat(findItemResponse.getProducts().get(0).getInsense()).isEqualTo(saved.getItemProducts().get(0).getProduct().getInsense());
        assertThat(findItemResponse.getProducts().get(0).getThroat()).isEqualTo(saved.getItemProducts().get(0).getProduct().getThroat());
        assertThat(findItemResponse.getCategory().getFirstName()).isEqualTo(saved.getCategory().getFirstName());
        assertThat(findItemResponse.getCategory().getMiddleName()).isEqualTo(saved.getCategory().getMiddleName());
        assertThat(findItemResponse.getCategory().getLastName()).isEqualTo(saved.getCategory().getLastName());
    }
}