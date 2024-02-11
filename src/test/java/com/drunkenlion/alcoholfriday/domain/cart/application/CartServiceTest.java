package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.AddCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@Transactional
class CartServiceImplTest {
    @InjectMocks
    private CartServiceImpl cartService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartDetailRepository cartDetailRepository;
    @Mock
    private ItemRepository itemRepository;

    // test를 위한 임의 변수
    /*private Member member;
    private List<CartDetail> cartDetails = new ArrayList<>();
    private Cart cart;
    @JoinColumn(name = "item_id",foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Item item;
    @ColumnDefault("0")
    private Long quantity;*/
    // Item
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
    private final Long balence = 10L;
    private final Long insense = 10L;
    private final Long throat = 10L;

    // Member
    private final Long id = 1L;
    private final String email = "test@example.com";
    private final String provider = "kakao_test12345";
    private final String name = "테스트";
    private final String nickname = "test";
    private final String role = "MEMBER";
    private final Long phone = 1012345678L;
    private final LocalDate certifyAt = null;
    private final boolean agreedToServiceUse = false;
    private final boolean agreedToServicePolicy = false;
    private final boolean agreedToServicePolicyUse = false;
    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = null;
    private final LocalDateTime deletedAt = null;
    private final int page = 0;
    private final int size = 20;

    @Test
    @DisplayName("장바구니에 한개 담기")
    void addCart() {
        // given
        Mockito.when(this.cartRepository.findFirstByMember(any(Member.class))).thenReturn(this.getOneCart());
        Mockito.when(itemRepository.findById(any(Long.class))).thenReturn(this.getOneItem());

        List<AddCartRequest> cartRequests = new ArrayList<>();
        AddCartRequest cartRequest1 = AddCartRequest.builder()
                .itemId(1L)
                .quantity(2L)
                .build();

        cartRequests.add(cartRequest1);

        // when
        List<CartDetailResponse> cartDetailResponse = cartService.addCartList(cartRequests, getOneMember().get());

        // then
        assertThat(cartDetailResponse).isNotNull();
        assertThat(cartDetailResponse.get(1).getItem()).isEqualTo(getOneItem());
    }

    @Test
    @DisplayName("장바구니에 여러개 담기")
    @Disabled
    void addCartList() {
        // given
        Member member = Member.builder().build();
        List<AddCartRequest> cartRequests = new ArrayList<>();

        AddCartRequest cartRequest1 = AddCartRequest.builder()
                .itemId(1L)
                .quantity(2L)
                .build();

        AddCartRequest cartRequest2 = AddCartRequest.builder()
                .itemId(2L)
                .quantity(1L)
                .build();

        cartRequests.add(cartRequest1);
        cartRequests.add(cartRequest2);

//        when(cartRepository.findFirstByMember(any(Member.class))).thenReturn(null);
//        when(cartRepository.save(any(Cart.class))).thenReturn(Cart.builder().build());

        // when
        List<CartDetailResponse> cartDetailResponses = cartService.addCartList(cartRequests, member);

        // then
        assertThat(cartDetailResponses).isNotNull();
        assertThat(cartDetailResponses.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Item 테이블에 없는 item인 경우")
    void ifItemNull() {
    }

    // 장바구니가 이미 있는 경우
    // 장바구니가 없는 경우


    private Optional<Item> getOneItem() {
        return Optional.of(this.getDataItem());
    }

    private Item getDataItem() {
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
                .balence(balence)
                .insense(insense)
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

    private Optional<Cart> getOneCart() {
        return Optional.of(this.getMemberCart());
    }

    private Cart getMemberCart() {
        Member member = Member.builder()
                .id(id)
                .email(email)
                .provider(provider)
                .name(name)
                .nickname(nickname)
                .role(role)
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();

        return Cart.builder()
                .member(member)
                .build();
    }

    private Optional<Member> getOneMember() {
        return Optional.of(this.getDataMember());
    }

    private Member getDataMember() {
       return Member.builder()
                .id(id)
                .email(email)
                .provider(provider)
                .name(name)
                .nickname(nickname)
                .role(role)
                .phone(phone)
                .certifyAt(certifyAt)
                .agreedToServiceUse(agreedToServiceUse)
                .agreedToServicePolicy(agreedToServicePolicy)
                .agreedToServicePolicyUse(agreedToServicePolicyUse)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .deletedAt(deletedAt)
                .build();
    }
}