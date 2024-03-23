package com.drunkenlion.alcoholfriday.domain.admin.item.application;

import com.drunkenlion.alcoholfriday.domain.admin.item.dto.*;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminItemServiceTest {
    @InjectMocks
    private AdminItemServiceImpl adminItemService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ItemProductRepository itemProductRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartDetailRepository cartDetailRepository;
    @Mock
    private FileService fileService;

    private final Long makerId = 1L;
    private final String makerName = "(주)국순당";
    private final String address = "강원도 횡성군 둔내면 강변로 975";
    private final String detail = "101";
    private final String region = "강원도";
    private final Long modifyMakerId = 2L;
    private final String modifyMakerName = "test 제조사 수정";
    private final String modifyAddress = "test 주소 수정";
    private final String modifyDetail = "test 상세주소 수정";
    private final String modifyRegion = "test 제조지역 수정";

    private final Long categoryFirstId = 1L;
    private final String firstName = "테스트 카테고리 대분류 1";
    private final Long modifyCategoryFirstId = 2L;
    private final String modifyFirstName = "테스트 카테고리 대분류 1 수정";

    private final Long categoryLastId = 1L;
    private final String lastName = "테스트 카테고리 소분류1";
    private final Long modifyCategoryLastId = 2L;
    private final String modifyLastName = "테스트 카테고리 소분류1 수정";

    private final Long productId = 1L;
    private final String productName = "1000억 막걸리 프리바이오";
    private final BigDecimal productPrice = BigDecimal.valueOf(10000);
    private final Long productQuantity = 1000L;
    private final Double alcohol = 10.0D;
    private final String ingredient = "쌀(국내산), 밀(국내산), 누룩, 정제수";
    private final Long sweet = 10L;
    private final Long sour = 10L;
    private final Long cool = 10L;
    private final Long body = 10L;
    private final Long balance = 10L;
    private final Long incense = 10L;
    private final Long throat = 10L;

    private final Long modifyProductId = 2L;
    private final String modifyProductName = "1000억 막걸리 프리바이오 수정";
    private final BigDecimal modifyProductPrice = BigDecimal.valueOf(1000);
    private final Long modifyProductQuantity = 100L;
    private final Double modifyAlcohol = 1.0D;
    private final String modifyIngredient = "쌀(국내산), 밀(국내산), 누룩, 정제수 수정";
    private final Long modifySweet = 1L;
    private final Long modifySour = 1L;
    private final Long modifyCool = 1L;
    private final Long modifyBody = 1L;
    private final Long modifyBalance = 1L;
    private final Long modifyIncense = 1L;
    private final Long modifyThroat = 1L;

    private final Long itemId = 1L;
    private final ItemType itemType = ItemType.REGULAR;
    private final String itemName = "프리바이오 막걸리 10개";
    private final BigDecimal itemPrice = BigDecimal.valueOf(20000);
    private final String itemInfo = "국순당 프리바이오 막걸리 10개입";

    private final Long modifyItemId = 2L;
    private final ItemType modifyItemType = ItemType.PROMOTION;
    private final String modifyItemName = "프리바이오 막걸리 10개 수정";
    private final BigDecimal modifyItemPrice = BigDecimal.valueOf(10000);
    private final String modifyItemInfo = "국순당 프리바이오 막걸리 10개입 수정";

    private final Long itemProductId = 1L;
    private final Long itemProductQuantity = 100L;
    private final Long modifyItemProductId = 2L;
    private final Long modifyItemProductQuantity = 200L;

    private final int page = 0;
    private final int size = 20;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();
    private final LocalDateTime deletedAt = LocalDateTime.now();

    @Test
    @DisplayName("상품 목록 조회 성공")
    public void getItemsTest() {
        // given
        when(this.itemRepository.findAll(any(Pageable.class))).thenReturn(this.getItems());

        // when
        Page<ItemListResponse> items = this.adminItemService.getItems(page, size);

        // then
        List<ItemListResponse> content = items.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(itemId);
        assertThat(content.get(0).getCategoryLastName()).isEqualTo(lastName);
        assertThat(content.get(0).getName()).isEqualTo(itemName);
        assertThat(content.get(0).getPrice()).isEqualTo(itemPrice);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("상품 상세 조회 성공")
    public void getItemTest() {
        // given
        when(this.itemRepository.findById(any())).thenReturn(this.getItemOne());

        // when
        ItemDetailResponse itemDetailResponse = this.adminItemService.getItem(itemId);

        // then
        assertThat(itemDetailResponse.getId()).isEqualTo(itemId);
        assertThat(itemDetailResponse.getItemProductInfos().get(0).getProductId()).isEqualTo(itemProductId);
        assertThat(itemDetailResponse.getItemProductInfos().get(0).getQuantity()).isEqualTo(itemProductQuantity);
        assertThat(itemDetailResponse.getCategoryLastId()).isEqualTo(categoryLastId);
        assertThat(itemDetailResponse.getCategoryFirstName()).isEqualTo(firstName);
        assertThat(itemDetailResponse.getCategoryLastName()).isEqualTo(lastName);
        assertThat(itemDetailResponse.getName()).isEqualTo(itemName);
        assertThat(itemDetailResponse.getPrice()).isEqualTo(itemPrice);
        assertThat(itemDetailResponse.getInfo()).isEqualTo(itemInfo);
        assertThat(itemDetailResponse.getType()).isEqualTo(itemType);
        assertThat(itemDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(itemDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("상품 상세 조회 실패 - 찾을 수 없는 상품")
    public void getItemFailNotFoundTest() {
        // given
        when(this.itemRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminItemService.getItem(itemId);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_ITEM.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_ITEM.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("상품 등록 성공")
    public void createItemTest() {
        // given
        ItemCreateRequest itemCreateRequest = ItemCreateRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(productId)
                                .quantity(itemProductQuantity)
                                .build()
                ))
                .categoryLastId(categoryLastId)
                .name(itemName)
                .price(itemPrice)
                .info(itemInfo)
                .type(itemType)
                .build();

        List<MultipartFile> files = new ArrayList<>();

        when(categoryRepository.findByIdAndDeletedAtIsNull(categoryLastId)).thenReturn(this.getCategoryOne());
        when(productRepository.findByIdAndDeletedAtIsNull(productId)).thenReturn(this.getProductOne());
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemProductRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ItemDetailResponse itemDetailResponse = adminItemService.createItem(itemCreateRequest, files);

        // then
        assertThat(itemDetailResponse.getItemProductInfos().get(0).getProductId()).isEqualTo(itemProductId);
        assertThat(itemDetailResponse.getItemProductInfos().get(0).getQuantity()).isEqualTo(itemProductQuantity);
        assertThat(itemDetailResponse.getCategoryLastId()).isEqualTo(categoryLastId);
        assertThat(itemDetailResponse.getCategoryFirstName()).isEqualTo(firstName);
        assertThat(itemDetailResponse.getCategoryLastName()).isEqualTo(lastName);
        assertThat(itemDetailResponse.getName()).isEqualTo(itemName);
        assertThat(itemDetailResponse.getPrice()).isEqualTo(itemPrice);
        assertThat(itemDetailResponse.getInfo()).isEqualTo(itemInfo);
        assertThat(itemDetailResponse.getType()).isEqualTo(itemType);
    }

    @Test
    @DisplayName("상품 등록 실패 - 찾을 수 없는 카테고리 소분류")
    public void createItemFailCategoryNotFoundTest() {
        // given
        ItemCreateRequest itemCreateRequest = ItemCreateRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(productId)
                                .quantity(itemProductQuantity)
                                .build()
                ))
                .categoryLastId(categoryLastId)
                .build();

        when(categoryRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminItemService.createItem(itemCreateRequest, any());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("상품 등록 실패 - 찾을 수 없는 제품")
    public void createItemFailProductNotFoundTest() {
        // given
        ItemCreateRequest itemCreateRequest = ItemCreateRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(productId)
                                .quantity(itemProductQuantity)
                                .build()
                ))
                .categoryLastId(categoryLastId)
                .name(itemName)
                .price(itemPrice)
                .info(itemInfo)
                .type(itemType)
                .build();

        when(categoryRepository.findByIdAndDeletedAtIsNull(categoryLastId)).thenReturn(this.getCategoryOne());
        when(productRepository.findByIdAndDeletedAtIsNull(productId)).thenReturn(Optional.empty());
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminItemService.createItem(itemCreateRequest, any());
        });
        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("상품 수정 성공")
    public void modifyItemTest() {
        // given
        ItemModifyRequest itemModifyRequest = ItemModifyRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(modifyProductId)
                                .quantity(modifyItemProductQuantity)
                                .build()
                ))
                .categoryLastId(modifyCategoryLastId)
                .name(modifyItemName)
                .price(modifyItemPrice)
                .info(modifyItemInfo)
                .type(modifyItemType)
                .build();

        List<Integer> remove = new ArrayList<>();
        List<MultipartFile> files = new ArrayList<>();

        when(itemRepository.findByIdAndDeletedAtIsNull(itemId)).thenReturn(this.getItemOne());
        when(categoryRepository.findByIdAndDeletedAtIsNull(modifyCategoryLastId)).thenReturn(this.getModifyCategoryOne());
        when(productRepository.findByIdAndDeletedAtIsNull(modifyProductId)).thenReturn(this.getModifyProductOne());
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(itemProductRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ItemDetailResponse itemDetailResponse = adminItemService.modifyItem(itemId, itemModifyRequest, remove, files);

        // then
        assertThat(itemDetailResponse.getItemProductInfos().get(0).getProductId()).isEqualTo(modifyItemProductId);
        assertThat(itemDetailResponse.getItemProductInfos().get(0).getQuantity()).isEqualTo(modifyItemProductQuantity);
        assertThat(itemDetailResponse.getCategoryLastId()).isEqualTo(modifyCategoryLastId);
        assertThat(itemDetailResponse.getCategoryFirstName()).isEqualTo(modifyFirstName);
        assertThat(itemDetailResponse.getCategoryLastName()).isEqualTo(modifyLastName);
        assertThat(itemDetailResponse.getName()).isEqualTo(modifyItemName);
        assertThat(itemDetailResponse.getPrice()).isEqualTo(modifyItemPrice);
        assertThat(itemDetailResponse.getInfo()).isEqualTo(modifyItemInfo);
        assertThat(itemDetailResponse.getType()).isEqualTo(modifyItemType);
    }

    @Test
    @DisplayName("상품 수정 실패 - 찾을 수 없는 상품")
    public void modifyItemFailItemNotFoundTest() {
        // given
        ItemModifyRequest itemModifyRequest = ItemModifyRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(modifyProductId)
                                .quantity(modifyItemProductQuantity)
                                .build()
                ))
                .build();

        Mockito.when(this.itemRepository.findByIdAndDeletedAtIsNull(itemId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminItemService.modifyItem(itemId, itemModifyRequest, null, null);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_ITEM.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_ITEM.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("상품 수정 실패 - 찾을 수 없는 카테고리 소분류")
    public void modifyItemFailCategoryNotFoundTest() {
        // given
        ItemModifyRequest itemModifyRequest = ItemModifyRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(modifyProductId)
                                .quantity(modifyItemProductQuantity)
                                .build()
                ))
                .categoryLastId(modifyCategoryLastId)
                .build();

        when(itemRepository.findByIdAndDeletedAtIsNull(itemId)).thenReturn(this.getItemOne());
        when(categoryRepository.findByIdAndDeletedAtIsNull(modifyCategoryLastId)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminItemService.modifyItem(itemId, itemModifyRequest, null, null);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("상품 수정 실패 - 찾을 수 없는 제품")
    public void modifyItemFailProductNotFoundTest() {
        // given
        ItemModifyRequest itemModifyRequest = ItemModifyRequest.builder()
                .itemProductInfos(List.of(
                        ItemProductInfo.builder()
                                .productId(modifyProductId)
                                .quantity(modifyItemProductQuantity)
                                .build()
                ))
                .categoryLastId(modifyCategoryLastId)
                .build();

        when(itemRepository.findByIdAndDeletedAtIsNull(itemId)).thenReturn(this.getItemOne());
        when(categoryRepository.findByIdAndDeletedAtIsNull(modifyCategoryLastId)).thenReturn(this.getModifyCategoryOne());
        when(productRepository.findByIdAndDeletedAtIsNull(modifyProductId)).thenReturn(Optional.empty());

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminItemService.modifyItem(itemId, itemModifyRequest, null, null);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("상품 삭제 성공")
    public void deleteItemTest() {
        // given
        Item item = this.getItemData();

        when(itemRepository.findByIdAndDeletedAtIsNull(itemId)).thenReturn(this.getItemOne());
        when(itemProductRepository.findByItemAndDeletedAtIsNull(item)).thenReturn(this.getItemProducts());
        when(cartDetailRepository.findByItemAndDeletedAtIsNull(item)).thenReturn(this.getCartDetails());
        ArgumentCaptor<Item> itemCaptor = ArgumentCaptor.forClass(Item.class);
        ArgumentCaptor<List<ItemProduct>> itemProductCaptor = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<CartDetail>> cartDetailCaptor = ArgumentCaptor.forClass(List.class);

        // When
        adminItemService.deleteItem(itemId);

        // then
        verify(itemRepository).save(itemCaptor.capture());
        Item savedItem = itemCaptor.getValue();
        assertThat(savedItem.getDeletedAt()).isNotNull();

        verify(itemProductRepository).saveAll(itemProductCaptor.capture());
        List<ItemProduct> savedItemProducts = itemProductCaptor.getValue();
        assertThat(savedItemProducts).allSatisfy(itemProduct -> assertThat(itemProduct.getDeletedAt()).isNotNull());

        verify(cartDetailRepository).saveAll(cartDetailCaptor.capture());
        List<CartDetail> savedCartDetails = cartDetailCaptor.getValue();
        assertThat(savedCartDetails).allSatisfy(cartDetail -> assertThat(cartDetail.getDeletedAt()).isNotNull());
    }

    @Test
    @DisplayName("상품 삭제 실패 - 찾을 수 없는 상품")
    public void deleteItemFailItemNotFoundTest() {
        // given
        Item item = this.getItemData();

        when(itemRepository.findByIdAndDeletedAtIsNull(itemId)).thenReturn(Optional.empty());

        // When
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminItemService.deleteItem(itemId);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_ITEM.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_ITEM.getMessage(), exception.getMessage());
    }

    private Page<Item> getItems() {
        List<Item> list = List.of(this.getItemData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Item>(list, pageable, list.size());
    }

    private List<ItemProduct> getItemProducts() {
        return List.of(this.getItemProductData());
    }

    private List<CartDetail> getCartDetails() {
        return List.of(this.getCartDetailData());
    }

    private Optional<Item> getItemOne() {
        return Optional.of(this.getItemData());
    }

    private Optional<Product> getProductOne() {
        return Optional.of(this.getProductData());
    }

    private Optional<Product> getModifyProductOne() {
        return Optional.of(this.getModifyProductData());
    }

    private Optional<Category> getCategoryOne() {
        return Optional.of(this.getCategoryData());
    }

    private Optional<Category> getModifyCategoryOne() {
        return Optional.of(this.getModifyCategoryData());
    }

    private Maker getMakerData() {
        return Maker.builder()
                .id(makerId)
                .name(makerName)
                .address(address)
                .detail(detail)
                .region(region)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Maker getModifyMakerData() {
        return Maker.builder()
                .id(modifyMakerId)
                .name(modifyMakerName)
                .address(modifyAddress)
                .detail(modifyDetail)
                .region(modifyRegion)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private CategoryClass getCategoryClassData() {
        return CategoryClass.builder()
                .id(categoryFirstId)
                .firstName(firstName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private CategoryClass getModifyCategoryClassData() {
        return CategoryClass.builder()
                .id(modifyCategoryFirstId)
                .firstName(modifyFirstName)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Category getCategoryData() {
        CategoryClass categoryClass = getCategoryClassData();

        return Category.builder()
                .id(categoryLastId)
                .lastName(lastName)
                .categoryClass(categoryClass)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Category getModifyCategoryData() {
        CategoryClass categoryClass = getModifyCategoryClassData();

        return Category.builder()
                .id(modifyCategoryLastId)
                .lastName(modifyLastName)
                .categoryClass(categoryClass)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Product getProductData() {
        Maker maker = getMakerData();
        Category category = getCategoryData();

        return Product.builder()
                .id(productId)
                .name(productName)
                .price(productPrice)
                .quantity(productQuantity)
                .alcohol(alcohol)
                .ingredient(ingredient)
                .sweet(sweet)
                .sour(sour)
                .cool(cool)
                .body(body)
                .balance(balance)
                .incense(incense)
                .throat(throat)
                .maker(maker)
                .category(category)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Product getModifyProductData() {
        Maker modifyMaker = getModifyMakerData();
        Category modifyCategory = getModifyCategoryData();

        return Product.builder()
                .id(modifyProductId)
                .name(modifyProductName)
                .price(modifyProductPrice)
                .quantity(modifyProductQuantity)
                .alcohol(modifyAlcohol)
                .ingredient(modifyIngredient)
                .sweet(modifySweet)
                .sour(modifySour)
                .cool(modifyCool)
                .body(modifyBody)
                .balance(modifyBalance)
                .incense(modifyIncense)
                .throat(modifyThroat)
                .maker(modifyMaker)
                .category(modifyCategory)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private Item getItemData() {
        Category category = getCategoryData();
        Product product = getProductData();

        return Item.builder()
                .id(itemId)
                .type(itemType)
                .name(itemName)
                .price(itemPrice)
                .info(itemInfo)
                .category(category)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .itemProducts(List.of(
                        ItemProduct.builder()
                                .product(product)
                                .quantity(itemProductQuantity)
                                .build()
                ))
                .build();
    }

    private ItemProduct getItemProductData() {
        Item item = getItemData();
        Product product = getProductData();

        return ItemProduct.builder()
                .id(itemProductId)
                .item(item)
                .product(product)
                .quantity(itemProductQuantity)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private CartDetail getCartDetailData() {
        Item item = getItemData();

        return CartDetail.builder()
                .item(item)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
