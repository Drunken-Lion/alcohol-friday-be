package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional(readOnly = true)
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemProductRepository itemProductRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void beforeEach() {
        Category category = Category.builder()
                .firstName("식품")
                .middleName("전통주")
                .lastName("탁주")
                .build();
        categoryRepository.save(category);
        categoryRepository.flush();

        Product product = Product.builder()
                .category(category)
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
        productRepository.save(product);
        productRepository.flush();

        Item item = Item.builder()
                .category(category)
                .name("test ddaattaa")
                .price(new BigDecimal(50000))
                .info("이 상품은 테스트 상품입니다.")
                .build();
        itemRepository.save(item);
        itemRepository.flush();

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .build();
        itemProductRepository.save(itemProduct);
        itemProductRepository.flush();
    }

    @AfterEach
    void afterEach() {
        itemProductRepository.deleteAll();
        itemRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    @Transactional
    void searchTest() {
        // given
        List<String> keywordType = new ArrayList<>();
        keywordType.add("type");
        keywordType.add("name");

        String keyword = "탁주";

        Pageable pageable = PageRequest.ofSize(10);
        // when
        Page<Item> search = itemRepository.search(keywordType, keyword, pageable);
        // then
        assertThat(search.getContent()).isInstanceOf(List.class);
        assertThat(search.getContent().size()).isEqualTo(1);
    }
}