package com.drunkenlion.alcoholfriday.domain.item.dao;

import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryClassRepository;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
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
@Transactional
class ItemRepositoryTest {
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

    @BeforeEach
    @Transactional
    void beforeEach() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName("식품")
                .build();

        Category category = Category.builder()
                .lastName("탁주/막걸리")
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name("1000억 유산균막걸리")
                .quantity(10L)
                .alcohol(17.0D)
                .ingredient("알콜, 누룩 등등...")
                .sweet(1L)
                .sour(1L)
                .cool(1L)
                .body(1L)
                .balance(1L)
                .incense(1L)
                .throat(1L)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name("1000억 유산균막걸리 3개입")
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

        categoryClassRepository.save(categoryClass);
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
        categoryClassRepository.deleteAll();
    }

    @Test
    void searchTest() {
        // given
        List<String> categories = new ArrayList<>();
        categories.add("탁주/막걸리");

        String keyword = "1000억 유산균막걸리";

        Pageable pageable = PageRequest.ofSize(12);
        // when
        Page<Item> search = itemRepository.search(categories, keyword, pageable);
        // then
        assertThat(search.getContent()).isInstanceOf(List.class);
        assertThat(search.getContent().size()).isEqualTo(1);
        assertThat(search.getContent().get(0).getCategory().getCategoryClass().getFirstName()).isEqualTo("식품");
        assertThat(search.getContent().get(0).getCategory().getLastName()).isEqualTo("탁주/막걸리");
        assertThat(search.getContent().get(0).getName()).isEqualTo("1000억 유산균막걸리 3개입");
        assertThat(search.getContent().get(0).getItemProducts().get(0).getProduct().getName()).isEqualTo("1000억 유산균막걸리");
    }

    @Test
    void getTest() {
        // given
        Item saved = itemRepository.findAll().get(0);

        // when
        Item item = itemRepository.findById(saved.getId())
                .orElseThrow(() -> new IllegalArgumentException("fail"));

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getItemProducts().isEmpty()).isFalse();
        assertThat(item.getId()).isEqualTo(saved.getId());
        assertThat(item.getName()).isEqualTo(saved.getName());
        assertThat(item.getPrice()).isEqualTo(saved.getPrice());
        assertThat(item.getInfo()).isEqualTo(saved.getInfo());
        assertThat(item.getItemProducts().isEmpty()).isFalse();
        assertThat(item.getItemProducts().get(0).getProduct().getName()).isEqualTo(saved.getItemProducts().get(0).getProduct().getName());
        assertThat(item.getItemProducts().get(0).getProduct().getQuantity()).isEqualTo(saved.getItemProducts().get(0).getProduct().getQuantity());
        assertThat(item.getItemProducts().get(0).getProduct().getAlcohol()).isEqualTo(saved.getItemProducts().get(0).getProduct().getAlcohol());
        assertThat(item.getItemProducts().get(0).getProduct().getIngredient()).isEqualTo(saved.getItemProducts().get(0).getProduct().getIngredient());
        assertThat(item.getItemProducts().get(0).getProduct().getSweet()).isEqualTo(saved.getItemProducts().get(0).getProduct().getSweet());
        assertThat(item.getItemProducts().get(0).getProduct().getSour()).isEqualTo(saved.getItemProducts().get(0).getProduct().getSour());
        assertThat(item.getItemProducts().get(0).getProduct().getCool()).isEqualTo(saved.getItemProducts().get(0).getProduct().getCool());
        assertThat(item.getItemProducts().get(0).getProduct().getBody()).isEqualTo(saved.getItemProducts().get(0).getProduct().getBody());
        assertThat(item.getItemProducts().get(0).getProduct().getBalance()).isEqualTo(saved.getItemProducts().get(0).getProduct().getBalance());
        assertThat(item.getItemProducts().get(0).getProduct().getIncense()).isEqualTo(saved.getItemProducts().get(0).getProduct().getIncense());
        assertThat(item.getItemProducts().get(0).getProduct().getThroat()).isEqualTo(saved.getItemProducts().get(0).getProduct().getThroat());
        assertThat(item.getCategory().getCategoryClass().getFirstName()).isEqualTo(saved.getCategory().getCategoryClass().getFirstName());
        assertThat(item.getCategory().getLastName()).isEqualTo(saved.getCategory().getLastName());
    }
}