package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.entity.Cart;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
class CartServiceTest {
    @InjectMocks
    private CartServiceImpl cartService;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartDetailRepository cartDetailRepository;
    @Mock
    private ItemRepository itemRepository;

    // test를 위한 임의 변수
    // Item
    private final Long itemId1 = 1L;
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

    // Item2
    private final Long itemId2 = 2L;
    private final String firstName2 = "식품";
    private final String lastName2 = "청주";
    private final String productName2 = "test data2";
    private final String itemName2 = "test ddaattaa";
    private final BigDecimal price2 = new BigDecimal(100_000);
    private final String info2 = "이 상품은 테스트 상품2 입니다.";
    private final Long quantity2 = 10L;
    private final Long alcohol2 = 17L;
    private final String ingredient2 = "알콜, 누룩 등등...";
    private final Long sweet2 = 10L;
    private final Long sour2 = 10L;
    private final Long cool2 = 10L;
    private final Long body2 = 10L;
    private final Long balence2 = 10L;
    private final Long insense2 = 10L;
    private final Long throat2 = 10L;

    // Member
    private final Long id = 1L;
    private final String email = "test@example.com";
    private final String provider = ProviderType.KAKAO.getProviderName();
    private final String name = "테스트";
    private final String nickname = "test";
    private final String role = MemberRole.MEMBER.getRole();
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

    // Cart
    private final Member member = getDataMember();

    // CartDetail
    private final Cart cart = getDataCart();
    private final Item item = getDataItem();
    private final Item item2 = getDataItem2();
    private final Long quantityCart = 2L;
    private final Long quantityCart2 = 1L;


    @Test
    @DisplayName("장바구니에 한 개 상품 담았을 경우")
    void addCartTest() {
        // given
        List<CartRequest> cartDetails = new ArrayList<>();
        CartRequest cartRequest = CartRequest.builder()
                .itemId(itemId1)
                .quantity(quantityCart)
                .build();
        cartDetails.add(cartRequest);

        when(this.itemRepository.findById(cartRequest.getItemId())).thenReturn(this.getOneItem());

        CartDetail cartDetail = CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(cartRequest.getQuantity())
                .build();
        when(this.cartDetailRepository.save(any(CartDetail.class))).thenReturn(cartDetail);

        // when
        List<CartDetailResponse> cartDetailResponses = this.cartService.addCartList(cartDetails, member);

        // then
        assertThat(cartDetailResponses.get(0).getItem().getName()).isEqualTo(itemName);
        assertThat(cartDetailResponses.get(0).getQuantity()).isEqualTo(cartRequest.getQuantity());
    }

    @Test
    @DisplayName("장바구니에 한 개 이상 상품 담았을 경우")
    void addCartListTest() {
        // given
        List<CartRequest> cartDetails = new ArrayList<>();
        CartRequest cartRequest1 = CartRequest.builder()
                .itemId(itemId1)
                .quantity(quantityCart)
                .build();
        CartRequest cartRequest2 = CartRequest.builder()
                .itemId(itemId2)
                .quantity(quantityCart2)
                .build();

        cartDetails.add(cartRequest1);
        cartDetails.add(cartRequest2);

        when(this.itemRepository.findById(cartRequest1.getItemId())).thenReturn(this.getOneItem());
        when(this.itemRepository.findById(cartRequest2.getItemId())).thenReturn(this.getOneItem2());

        CartDetail cartDetail1 = CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(cartRequest1.getQuantity())
                .build();
        CartDetail cartDetail2 = CartDetail.builder()
                .cart(cart)
                .item(item2)
                .quantity(cartRequest2.getQuantity())
                .build();

        when(this.cartDetailRepository.save(any(CartDetail.class))).thenReturn(cartDetail1).thenReturn(cartDetail2);

        // when
        List<CartDetailResponse> cartDetailResponses = this.cartService.addCartList(cartDetails, member);

        // then
        assertThat(cartDetailResponses.get(0).getItem().getName()).isEqualTo(itemName);
        assertThat(cartDetailResponses.get(0).getQuantity()).isEqualTo(cartRequest1.getQuantity());
        assertThat(cartDetailResponses.get(1).getItem().getName()).isEqualTo(itemName2);
        assertThat(cartDetailResponses.get(1).getQuantity()).isEqualTo(cartRequest2.getQuantity());
    }

    @Test
    @DisplayName("장바구니에서 한 개 상품 수량 변경")
    void modifyCartItemQuantityTest() {
        // given
        // cartRepository.findFirstByMember(member).orElse(null)
        when(this.cartRepository.findFirstByMember(member)).thenReturn(getOneCart());

        // itemRepository.findById(modifyCart.getItemId())
        List<CartRequest> cartDetails = new ArrayList<>();
        CartRequest cartRequest = CartRequest.builder()
                .itemId(itemId1)
                .quantity(5L)
                .build();
        cartDetails.add(cartRequest);

        when(this.itemRepository.findById(cartRequest.getItemId())).thenReturn(this.getOneItem());

        // cartDetailRepository.findByItemAndCart(item, cart)
        when(this.cartDetailRepository.findByItemAndCart(item, cart)).thenReturn(getDataCartDetail());

        // when
        CartDetail modifyCartItemDetail = this.cartService.modifyCartItemQuantity(cartRequest, member);

        // then
        assertThat(modifyCartItemDetail.getItem().getName()).isEqualTo(itemName);
        assertThat(modifyCartItemDetail.getQuantity()).isEqualTo(cartRequest.getQuantity());
    }

    @Test
    @DisplayName("장바구니가 없는 경우")
    void noCartTest() {
        // given
        // cartRepository.findFirstByMember(member).orElse(null)
        when(this.cartRepository.findFirstByMember(member)).thenReturn(Optional.empty());

        List<CartRequest> cartDetails = new ArrayList<>();
        CartRequest cartRequest1 = CartRequest.builder()
                .itemId(itemId1)
                .quantity(quantityCart)
                .build();
        CartRequest cartRequest2 = CartRequest.builder()
                .itemId(itemId2)
                .quantity(quantityCart2)
                .build();

        cartDetails.add(cartRequest1);
        cartDetails.add(cartRequest2);

        when(this.itemRepository.findById(cartRequest1.getItemId())).thenReturn(this.getOneItem());
        when(this.itemRepository.findById(cartRequest2.getItemId())).thenReturn(this.getOneItem2());

        // 카트 만들기
        Cart makeCart = Cart.create(member);
        when(this.cartRepository.save(makeCart)).thenReturn(cart);

        CartDetail cartDetail1 = CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(cartRequest1.getQuantity())
                .build();
        CartDetail cartDetail2 = CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(cartRequest2.getQuantity())
                .build();

        when(this.cartDetailRepository.save(any(CartDetail.class))).thenReturn(cartDetail1).thenReturn(cartDetail2);

        // when
        List<CartDetailResponse> cartDetailResponses = this.cartService.addCartList(cartDetails, member);

        // then
        assertThat(cartDetailResponses.get(0).getItem().getName()).isEqualTo(itemName);
        assertThat(cartDetailResponses.get(0).getQuantity()).isEqualTo(cartRequest1.getQuantity());
        assertThat(cartDetailResponses.get(1).getItem().getName()).isEqualTo(itemName2);
        assertThat(cartDetailResponses.get(1).getQuantity()).isEqualTo(cartRequest2.getQuantity());
    }


    private Optional<Cart> getOneCart() {
        return Optional.of(this.getDataCart());
    }

    private Cart getDataCart() {
        return Cart.builder()
                .member(member)
                .build();
    }

    private Optional<CartDetail> getOneCartDetail() {
        return Optional.of(this.getDataCartDetail());
    }

    private CartDetail getDataCartDetail() {
        return CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(quantityCart)
                .build();
    }

    private Optional<CartDetail> getOneCartDetail2() {
        return Optional.of(this.getDataCartDetail2());
    }

    private CartDetail getDataCartDetail2() {
        return CartDetail.builder()
                .cart(cart)
                .item(item2)
                .quantity(quantityCart2)
                .build();
    }
    private Optional<Member> getOneMember() {
        return Optional.of(this.getDataMember());
    }

    private Member getDataMember() {
        return Member.builder()
                .id(id)
                .email(email)
                .provider(ProviderType.ofProvider(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.ofRole(role))
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

    private Optional<Item> getOneItem2() {
        return Optional.of(this.getDataItem2());
    }

    private Item getDataItem2() {
        CategoryClass categoryClass = CategoryClass.builder()
                .firstName(firstName2)
                .build();

        Category category = Category.builder()
                .lastName(lastName2)
                .build();
        category.addCategoryClass(categoryClass);

        Product product = Product.builder()
                .name(productName2)
                .quantity(quantity2)
                .alcohol(alcohol2)
                .ingredient(ingredient2)
                .sweet(sweet2)
                .sour(sour2)
                .cool(cool2)
                .body(body2)
                .balence(balence2)
                .insense(insense2)
                .throat(throat2)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .name(itemName2)
                .price(price2)
                .info(info2)
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