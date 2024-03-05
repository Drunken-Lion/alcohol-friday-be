package com.drunkenlion.alcoholfriday.domain.item.application;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.file.application.FileServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
class ItemServiceTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private FileServiceImpl fileService;
    @Mock
    private ItemRepository itemRepository;

    // test를 위한 임의 변수
    private final String firstName = "식품";
    private final String lastName = "탁주";
    private final String productName = "test data";
    private final String itemName = "test ddaattaa";
    private final BigDecimal price = new BigDecimal(50000);
    private final String info = "이 상품은 테스트 상품입니다.";
    private final Long quantity = 10L;
    private final Long alcohol = 17L;
    private final String ingredient = "알콜, 누룩 등등...";
    private final Long sweet = 10L;
    private final Long sour = 10L;
    private final Long cool = 10L;
    private final Long body = 10L;
    private final Long balance = 10L;
    private final Long incense = 10L;
    private final Long throat = 10L;

    @Test
    void searchTest() {
        // given
        Mockito.when(this.itemRepository.search(any(), any(), any())).thenReturn(this.getSearch());

        List<String> list = new ArrayList<>();
        list.add("type");
        list.add("name");
        // when
        Page<SearchItemResponse> search = this.itemService.search(10, "탁주", list);
        // then
        List<SearchItemResponse> content = search.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getName()).isEqualTo(itemName);
        assertThat(content.get(0).getPrice()).isEqualTo(price);
        assertThat(content.get(0).getCategory().getFirstName()).isEqualTo(firstName);
        assertThat(content.get(0).getCategory().getLastName()).isEqualTo(lastName);
    }

    @Test
    void getTest() {
        // given
        Mockito.when(this.itemRepository.get(any())).thenReturn(this.getOne());
        // when
        FindItemResponse findItemResponse = this.itemService.get(1L);
        // then
        assertThat(findItemResponse.getName()).isEqualTo(itemName);
        assertThat(findItemResponse.getPrice()).isEqualTo(price);
        assertThat(findItemResponse.getInfo()).isEqualTo(info);
        assertThat(findItemResponse.getProducts().isEmpty()).isFalse();
        assertThat(findItemResponse.getProducts().get(0).getName()).isEqualTo(productName);
        assertThat(findItemResponse.getProducts().get(0).getQuantity()).isEqualTo(quantity);
        assertThat(findItemResponse.getProducts().get(0).getAlcohol()).isEqualTo(alcohol);
        assertThat(findItemResponse.getProducts().get(0).getIngredient()).isEqualTo(ingredient);
        assertThat(findItemResponse.getProducts().get(0).getSweet()).isEqualTo(sweet);
        assertThat(findItemResponse.getProducts().get(0).getSour()).isEqualTo(sour);
        assertThat(findItemResponse.getProducts().get(0).getCool()).isEqualTo(cool);
        assertThat(findItemResponse.getProducts().get(0).getBody()).isEqualTo(body);
        assertThat(findItemResponse.getProducts().get(0).getBalance()).isEqualTo(balance);
        assertThat(findItemResponse.getProducts().get(0).getIncense()).isEqualTo(incense);
        assertThat(findItemResponse.getProducts().get(0).getThroat()).isEqualTo(throat);
        assertThat(findItemResponse.getCategory().getFirstName()).isEqualTo(firstName);
        assertThat(findItemResponse.getCategory().getLastName()).isEqualTo(lastName);
    }

    private Page<Item> getSearch() {
        List<Item> list = List.of(this.getData());
        Pageable pageable = PageRequest.of(0, 10);
        return new PageImpl<Item>(list, pageable, list.size());
    }

    private Optional<Item> getOne() {
        return Optional.of(this.getData());
    }

    private Item getData() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName)
                .build();

        Category category = Category.builder()
                .lastName(lastName)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName)
                .quantity(quantity)
                .alcohol(alcohol)
                .ingredient(ingredient)
                .sweet(sweet)
                .sour(sour)
                .cool(cool)
                .body(body)
                .balance(balance)
                .incense(incense)
                .throat(throat)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name(itemName)
                .price(price)
                .info(info)
                .build();
        item.addCategory(category);

        ItemProduct itemProduct = ItemProduct.builder()
                .item(item)
                .product(product)
                .build();
        itemProduct.addItem(item);
        itemProduct.addProduct(product);

        return item;
    }
}