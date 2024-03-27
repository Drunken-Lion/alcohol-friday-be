package com.drunkenlion.alcoholfriday.domain.cart.application;

import com.drunkenlion.alcoholfriday.domain.auth.enumerated.ProviderType;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartRepository;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.CartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.request.DeleteCartRequest;
import com.drunkenlion.alcoholfriday.domain.cart.dto.response.CartDetailResponse;
import com.drunkenlion.alcoholfriday.domain.cart.dto.response.CartResponse;
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
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import org.junit.jupiter.api.Assertions;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private final Double alcohol = 17.0D;
    private final String ingredient = "알콜, 누룩 등등...";
    private final Long sweet = 10L;
    private final Long sour = 10L;
    private final Long cool = 10L;
    private final Long body = 10L;
    private final Long balance = 10L;
    private final Long incense = 10L;
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
    private final Double alcohol2 = 17.0D;
    private final String ingredient2 = "알콜, 누룩 등등...";
    private final Long sweet2 = 10L;
    private final Long sour2 = 10L;
    private final Long cool2 = 10L;
    private final Long body2 = 10L;
    private final Long balance2 = 10L;
    private final Long incense2 = 10L;
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
    private final Long cartId = 1L;
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
        when(cartRepository.findByMember(member)).thenReturn(getOneCart());

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
        CartResponse cartResponse = this.cartService.addCartList(cartDetails, member);

        // then
        assertThat(cartResponse.getCartId()).isEqualTo(cartId);
        assertThat(cartResponse.getCartDetailResponseList().get(0).getItem().getName()).isEqualTo(itemName);
        assertThat(cartResponse.getCartDetailResponseList().get(0).getQuantity()).isEqualTo(cartRequest.getQuantity());
    }

    @Test
    @DisplayName("장바구니에 한 개 이상 상품 담았을 경우")
    void addCartListTest() {
        // given
        when(cartRepository.findByMember(member)).thenReturn(getOneCart());

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
        CartResponse cartResponse = this.cartService.addCartList(cartDetails, member);

        // then
        assertThat(cartResponse.getCartId()).isEqualTo(cartId);
        assertThat(cartResponse.getCartDetailResponseList().get(0).getItem().getName()).isEqualTo(itemName);
        assertThat(cartResponse.getCartDetailResponseList().get(0).getQuantity()).isEqualTo(cartRequest1.getQuantity());
        assertThat(cartResponse.getCartDetailResponseList().get(1).getItem().getName()).isEqualTo(itemName2);
        assertThat(cartResponse.getCartDetailResponseList().get(1).getQuantity()).isEqualTo(cartRequest2.getQuantity());
    }

    @Test
    @DisplayName("장바구니에서 한 개 상품 수량 변경")
    void modifyCartItemQuantityTest() {
        // given
        // cartRepository.findByMember(member).orElse(null)
        when(this.cartRepository.findByMember(member)).thenReturn(getOneCart());

        // itemRepository.findById(modifyCart.getItemId())
        List<CartRequest> cartDetails = new ArrayList<>();
        CartRequest cartRequest = CartRequest.builder()
                .itemId(itemId1)
                .quantity(5L)
                .build();
        cartDetails.add(cartRequest);

        when(this.itemRepository.findById(cartRequest.getItemId())).thenReturn(this.getOneItem());

        // cartDetailRepository.findByItemAndCart(item, cart)
        when(this.cartDetailRepository.findByItemAndCart(item, cart)).thenReturn(getOneCartDetail());

        // when
        CartDetailResponse modifyCartItemDetail = this.cartService.modifyCartItemQuantity(cartRequest, member);

        // then
        assertThat(modifyCartItemDetail.getItem().getName()).isEqualTo(itemName);
        assertThat(modifyCartItemDetail.getQuantity()).isEqualTo(cartRequest.getQuantity());
    }

    @Test
    @DisplayName("장바구니가 없는 경우_장바구니 만들어서 저장")
    void noCartTest() {
        // given
        // cartRepository.findByMember(member).orElse(null)
        when(this.cartRepository.findByMember(member)).thenReturn(Optional.empty());

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
        Cart makeCart = Cart.builder()
                .member(member)
                .build();
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
        CartResponse cartResponse = this.cartService.addCartList(cartDetails, member);

        // then
        assertThat(cartResponse.getCartId()).isEqualTo(cartId);
        assertThat(cartResponse.getCartDetailResponseList().get(0).getItem().getName()).isEqualTo(itemName);
        assertThat(cartResponse.getCartDetailResponseList().get(0).getQuantity()).isEqualTo(cartRequest1.getQuantity());
        assertThat(cartResponse.getCartDetailResponseList().get(1).getItem().getName()).isEqualTo(itemName2);
        assertThat(cartResponse.getCartDetailResponseList().get(1).getQuantity()).isEqualTo(cartRequest2.getQuantity());
    }

    @Test
    @DisplayName("장바구니에 한 개 상품 조회하는 경우")
    void getCartDetailOneTest() {
        // given
        when(this.cartRepository.findByMember(any(Member.class))).thenReturn(this.getOneCart());

        List<CartDetail> cartDetails = new ArrayList<>();
        cartDetails.add(getDataCartDetail());
        when(this.cartDetailRepository.findAllByCart(any(Cart.class))).thenReturn(cartDetails);

        // when
        CartResponse cartList = this.cartService.getCartList(getDataMember());

        // then
        assertThat(cartList.getCartDetailResponseList().get(0).getQuantity()).isEqualTo(2L);
        assertThat(cartList.getCartDetailResponseList().get(0).getItem().getName()).isEqualTo(itemName);
        assertThat(cartList.getCartDetailResponseList().get(0).getItem().getPrice()).isEqualTo(price);
        assertThat(cartList.getTotalCartPrice()).isEqualTo(new BigDecimal("50000"));
        assertThat(cartList.getTotalCartQuantity()).isEqualTo(2);
    }

    @Test
    @DisplayName("장바구니에 한 개 이상 조회하는 경우")
    void getCartListTest() {
        // given
        when(this.cartRepository.findByMember(any(Member.class))).thenReturn(this.getOneCart());

        List<CartDetail> cartDetails = new ArrayList<>();
        cartDetails.add(getDataCartDetail());
        cartDetails.add(getDataCartDetail2());
        when(this.cartDetailRepository.findAllByCart(any(Cart.class))).thenReturn(cartDetails);

        // when
        CartResponse cartList = this.cartService.getCartList(getDataMember());

        // then
        assertThat(cartList.getCartDetailResponseList().get(0).getQuantity()).isEqualTo(2L);
        assertThat(cartList.getCartDetailResponseList().get(0).getItem().getName()).isEqualTo(itemName);
        assertThat(cartList.getCartDetailResponseList().get(0).getItem().getPrice()).isEqualTo(price);
        assertThat(cartList.getCartDetailResponseList().get(1).getQuantity()).isEqualTo(1L);
        assertThat(cartList.getCartDetailResponseList().get(1).getItem().getName()).isEqualTo(itemName2);
        assertThat(cartList.getCartDetailResponseList().get(1).getItem().getPrice()).isEqualTo(price2);
        assertThat(cartList.getTotalCartPrice()).isEqualTo(new BigDecimal("150000"));
        assertThat(cartList.getTotalCartQuantity()).isEqualTo(3);
    }

    @Test
    @DisplayName("회원에게 장바구니가 없는 경우")
    void getCartList_EmptyCartTest() {
        // given
        when(cartRepository.findByMember(any(Member.class))).thenReturn(Optional.empty());

        // when
        CartResponse cartList = this.cartService.getCartList(getDataMember());

        // then
        assertThat(cartList.getCartDetailResponseList()).isEqualTo(Collections.EMPTY_LIST);
        assertThat(cartList.getTotalCartPrice()).isEqualTo(new BigDecimal("0"));
        assertThat(cartList.getTotalCartQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("장바구니에 아무런 상품이 없는 경우")
    void getCartList_EmptyCartDetailTest() {
        // given
        when(cartRepository.findByMember(any(Member.class))).thenReturn(this.getOneCart());

        when(this.cartDetailRepository.findAllByCart(any(Cart.class))).thenReturn(Collections.EMPTY_LIST);

        // when
        CartResponse cartList = this.cartService.getCartList(getDataMember());

        // then
        assertThat(cartList.getCartDetailResponseList()).isEqualTo(Collections.EMPTY_LIST);
        assertThat(cartList.getTotalCartPrice()).isEqualTo(new BigDecimal("0"));
        assertThat(cartList.getTotalCartQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("장바구니에서 상품 한 개 삭제")
    void deleteCartOneItemTest() {
        // given
        // 상품을 장바구니에 저장
        CartDetail cartDetail = CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(quantityCart)
                .build();
        cartDetailRepository.save(cartDetail);

        // cartRepository.findByMember(member)
        when(cartRepository.findByMember(member)).thenReturn(getOneCart());
        // cartDetailRepository.deleteByIdAndCart(cartRequest.getItemId(), cart)
        List<DeleteCartRequest> cartRequests = new ArrayList<>();
        DeleteCartRequest cartRequest = DeleteCartRequest.builder()
                .itemId(itemId1)
                .build();
        cartRequests.add(cartRequest);

        when(itemRepository.findById(cartRequest.getItemId())).thenReturn(getOneItem());

        doNothing().when(cartDetailRepository).deleteByItemAndCart(getDataItem(), cart);

        // when
        cartService.deleteCartList(cartRequests, member);

        // then
        // 메서드 호출 여부를 검증
        verify(cartDetailRepository, times(1)).deleteByItemAndCart(item, cart);
    }

    @Test
    @DisplayName("장바구니에서 상품 한 개 이상 삭제")
    void deleteCartListTest() {
        // given
        // 상품을 장바구니에 저장
        CartDetail cartDetail = CartDetail.builder()
                .cart(cart)
                .item(item)
                .quantity(quantityCart)
                .build();
        CartDetail cartDetail2 = CartDetail.builder()
                .cart(cart)
                .item(item2)
                .quantity(quantityCart2)
                .build();
        cartDetailRepository.save(cartDetail);
        cartDetailRepository.save(cartDetail2);

        // cartRepository.findByMember(member)
        when(cartRepository.findByMember(member)).thenReturn(getOneCart());
        // cartDetailRepository.deleteByIdAndCart(cartRequest.getItemId(), cart)
        List<DeleteCartRequest> cartRequests = new ArrayList<>();
        DeleteCartRequest cartRequest = DeleteCartRequest.builder()
                .itemId(itemId1)
                .build();
        DeleteCartRequest cartRequest2 = DeleteCartRequest.builder()
                .itemId(itemId2)
                .build();
        cartRequests.add(cartRequest);
        cartRequests.add(cartRequest2);

        when(itemRepository.findById(cartRequest.getItemId())).thenReturn(getOneItem());
        when(itemRepository.findById(cartRequest2.getItemId())).thenReturn(getOneItem2());

        doNothing().when(cartDetailRepository).deleteByItemAndCart(getDataItem(), cart);
        doNothing().when(cartDetailRepository).deleteByItemAndCart(getDataItem2(), cart);

        // when
        cartService.deleteCartList(cartRequests, member);

        // then
        // 메서드 호출 여부를 검증
        verify(cartDetailRepository, times(1)).deleteByItemAndCart(item, cart);
        verify(cartDetailRepository, times(1)).deleteByItemAndCart(item2, cart);
    }

    @Test
    @DisplayName("삭제 시_장바구니가 없는 경우")
    void deleteCartList_EmptyCartTest() {
        // given
        List<DeleteCartRequest> cartRequests = new ArrayList<>();
        DeleteCartRequest cartRequest = DeleteCartRequest.builder()
                .itemId(itemId1)
                .build();
        cartRequests.add(cartRequest);

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            cartService.deleteCartList(cartRequests, member);
        });
    }

    @Test
    @DisplayName("삭제 시_장바구니 내역이 없는 경우")
    void deleteCartList_EmptyCartDetailTest() {
        // given
        DeleteCartRequest cartRequest = DeleteCartRequest.builder()
                .itemId(itemId1)
                .build();

        // 카트 만들기
        Cart makeCart = Cart.builder()
                .member(member)
                .build();

        // when & then
        Assertions.assertThrows(BusinessException.class, () -> {
            cartService.deleteCart(cartRequest, makeCart);
        });
    }

    @Test
    @DisplayName("회원의 장바구니 조회 - 성공")
    void getCart_success() {
        // given
        when(cartService.addFirstCart(getDataMember())).thenReturn(getOneCart());

        // when
        Cart cartResponse = cartService.getCart(getDataMember());

        // then
        assertThat(cartResponse.getId()).isEqualTo(cartId);
        assertThat(cartResponse.getMember()).isEqualTo(getDataMember());
    }

    @Test
    @DisplayName("회원의 장바구니 조회 - 실패")
    void getCart_fail() {
        // given
        when(cartService.addFirstCart(getDataMember())).thenReturn(Optional.empty());

        // when
        BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> {
            cartService.getCart(getDataMember());
        });

        // then
        assertThat(exception.getStatus()).isEqualTo(HttpResponse.Fail.NOT_FOUND_CART.getStatus());
        assertThat(exception.getMessage()).isEqualTo(HttpResponse.Fail.NOT_FOUND_CART.getMessage());
    }


    private Optional<Cart> getOneCart() {
        return Optional.of(this.getDataCart());
    }

    private Cart getDataCart() {
        return Cart.builder()
                .id(cartId)
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
                .provider(ProviderType.byProviderName(provider))
                .name(name)
                .nickname(nickname)
                .role(MemberRole.byRole(role))
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
                .balance(balance)
                .incense(incense)
                .throat(throat)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .id(itemId1)
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
                .balance(balance2)
                .incense(incense2)
                .throat(throat2)
                .build();
        product.addCategory(category);

        Item item = Item.builder()
                .id(itemId2)
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
