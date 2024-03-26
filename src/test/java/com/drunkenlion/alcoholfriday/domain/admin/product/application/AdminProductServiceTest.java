package com.drunkenlion.alcoholfriday.domain.admin.product.application;

import com.drunkenlion.alcoholfriday.domain.admin.product.dto.*;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.category.entity.CategoryClass;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.maker.dao.MakerRepository;
import com.drunkenlion.alcoholfriday.domain.maker.entity.Maker;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Transactional
public class AdminProductServiceTest {
    @InjectMocks
    private AdminProductServiceImpl adminProductService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private MakerRepository makerRepository;
    @Mock
    private ItemProductRepository itemProductRepository;
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

    private final Long id = 1L;
    private final String name = "1000억 막걸리 프리바이오";
    private final BigDecimal price = BigDecimal.valueOf(10000);
    private final BigDecimal distributionPrice = BigDecimal.valueOf(15000);
    private final Long quantity = 1000L;
    private final Double alcohol = 10.0D;
    private final String ingredient = "쌀(국내산), 밀(국내산), 누룩, 정제수";
    private final Long sweet = 10L;
    private final Long sour = 10L;
    private final Long cool = 10L;
    private final Long body = 10L;
    private final Long balance = 10L;
    private final Long incense = 10L;
    private final Long throat = 10L;

    private final String modifyName = "1000억 막걸리 프리바이오 수정";
    private final BigDecimal modifyPrice = BigDecimal.valueOf(1000);
    private final BigDecimal modifyDistributionPrice = BigDecimal.valueOf(1500);
    private final Long modifyQuantity = 100L;
    private final Double modifyAlcohol = 1.0D;
    private final String modifyIngredient = "쌀(국내산), 밀(국내산), 누룩, 정제수 수정";
    private final Long modifySweet = 1L;
    private final Long modifySour = 1L;
    private final Long modifyCool = 1L;
    private final Long modifyBody = 1L;
    private final Long modifyBalance = 1L;
    private final Long modifyIncense = 1L;
    private final Long modifyThroat = 1L;

    private final int page = 0;
    private final int size = 20;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private final LocalDateTime updatedAt = LocalDateTime.now();
    private final LocalDateTime deletedAt = LocalDateTime.now();

    @Test
    @DisplayName("제품 목록 조회 성공")
    public void getProductsTest() {
        // given
        Mockito.when(this.productRepository.findAll(any(Pageable.class))).thenReturn(this.getProducts());

        // when
        Page<ProductListResponse> products = this.adminProductService.getProducts(page, size);

        // then
        List<ProductListResponse> content = products.getContent();

        assertThat(content).isInstanceOf(List.class);
        assertThat(content.size()).isEqualTo(1);
        assertThat(content.get(0).getId()).isEqualTo(id);
        assertThat(content.get(0).getCategoryLastName()).isEqualTo(lastName);
        assertThat(content.get(0).getName()).isEqualTo(name);
        assertThat(content.get(0).getMakerName()).isEqualTo(makerName);
        assertThat(content.get(0).getCreatedAt()).isEqualTo(createdAt);
        assertThat(content.get(0).isDeleted()).isEqualTo(false);
    }

    @Test
    @DisplayName("제품 상세 조회 성공")
    public void getProductTest() {
        // given
        Mockito.when(this.productRepository.findById(any())).thenReturn(this.getProductOne());

        // when
        ProductDetailResponse productDetailResponse = this.adminProductService.getProduct(id);

        // then
        assertThat(productDetailResponse.getId()).isEqualTo(id);
        assertThat(productDetailResponse.getCategoryLastId()).isEqualTo(categoryLastId);
        assertThat(productDetailResponse.getCategoryFirstName()).isEqualTo(firstName);
        assertThat(productDetailResponse.getCategoryLastName()).isEqualTo(lastName);
        assertThat(productDetailResponse.getName()).isEqualTo(name);
        assertThat(productDetailResponse.getMakerId()).isEqualTo(makerId);
        assertThat(productDetailResponse.getMakerName()).isEqualTo(makerName);
        assertThat(productDetailResponse.getPrice()).isEqualTo(price);
        assertThat(productDetailResponse.getDistributionPrice()).isEqualTo(distributionPrice);
        assertThat(productDetailResponse.getQuantity()).isEqualTo(quantity);
        assertThat(productDetailResponse.getAlcohol()).isEqualTo(alcohol);
        assertThat(productDetailResponse.getIngredient()).isEqualTo(ingredient);
        assertThat(productDetailResponse.getSweet()).isEqualTo(sweet);
        assertThat(productDetailResponse.getSour()).isEqualTo(sour);
        assertThat(productDetailResponse.getCool()).isEqualTo(cool);
        assertThat(productDetailResponse.getBody()).isEqualTo(body);
        assertThat(productDetailResponse.getBalance()).isEqualTo(balance);
        assertThat(productDetailResponse.getIncense()).isEqualTo(incense);
        assertThat(productDetailResponse.getThroat()).isEqualTo(throat);
        assertThat(productDetailResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(productDetailResponse.getUpdatedAt()).isEqualTo(updatedAt);
    }

    @Test
    @DisplayName("제품 상세 조회 실패 - 찾을 수 없는 제품")
    public void getProductFailNotFoundTest() {
        // given
        Mockito.when(this.productRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.getProduct(id);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제품 등록 성공")
    public void createProductTest() {
        // given
        ProductCreateRequest productCreateRequest = ProductCreateRequest.builder()
                .categoryLastId(categoryLastId)
                .name(name)
                .makerId(makerId)
                .price(price)
                .distributionPrice(distributionPrice)
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

        List<MultipartFile> files = new ArrayList<>();

        when(categoryRepository.findByIdAndDeletedAtIsNull(categoryLastId)).thenReturn(this.getCategoryOne());
        when(makerRepository.findByIdAndDeletedAtIsNull(makerId)).thenReturn(this.getMakerOne());
        Mockito.when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductDetailResponse productDetailResponse = adminProductService.createProduct(productCreateRequest, files);

        // then
        assertThat(productDetailResponse.getCategoryLastId()).isEqualTo(categoryLastId);
        assertThat(productDetailResponse.getCategoryFirstName()).isEqualTo(firstName);
        assertThat(productDetailResponse.getCategoryLastName()).isEqualTo(lastName);
        assertThat(productDetailResponse.getName()).isEqualTo(name);
        assertThat(productDetailResponse.getMakerId()).isEqualTo(makerId);
        assertThat(productDetailResponse.getMakerName()).isEqualTo(makerName);
        assertThat(productDetailResponse.getPrice()).isEqualTo(price);
        assertThat(productDetailResponse.getDistributionPrice()).isEqualTo(distributionPrice);
        assertThat(productDetailResponse.getQuantity()).isEqualTo(0L);
        assertThat(productDetailResponse.getAlcohol()).isEqualTo(alcohol);
        assertThat(productDetailResponse.getIngredient()).isEqualTo(ingredient);
        assertThat(productDetailResponse.getSweet()).isEqualTo(sweet);
        assertThat(productDetailResponse.getSour()).isEqualTo(sour);
        assertThat(productDetailResponse.getCool()).isEqualTo(cool);
        assertThat(productDetailResponse.getBody()).isEqualTo(body);
        assertThat(productDetailResponse.getBalance()).isEqualTo(balance);
        assertThat(productDetailResponse.getIncense()).isEqualTo(incense);
        assertThat(productDetailResponse.getThroat()).isEqualTo(throat);
    }

    @Test
    @DisplayName("제품 등록 실패 - 찾을 수 없는 카테고리 소분류")
    public void createProductFailCategoryNotFoundTest() {
        // given
        ProductCreateRequest productCreateRequest = ProductCreateRequest.builder()
                .categoryLastId(categoryLastId)
                .makerId(makerId)
                .build();

        when(categoryRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.createProduct(productCreateRequest, any());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제품 등록 실패 - 찾을 수 없는 제조사")
    public void createProductFailMakerNotFoundTest() {
        // given
        ProductCreateRequest productCreateRequest = ProductCreateRequest.builder()
                .categoryLastId(categoryLastId)
                .makerId(makerId)
                .build();

        when(categoryRepository.findByIdAndDeletedAtIsNull(categoryLastId)).thenReturn(this.getCategoryOne());
        when(makerRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.createProduct(productCreateRequest, any());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제품 수정 성공")
    public void modifyProductTest() {
        // given
        ProductModifyRequest productModifyRequest = ProductModifyRequest.builder()
                .categoryLastId(modifyCategoryLastId)
                .name(modifyName)
                .makerId(modifyMakerId)
                .price(modifyPrice)
                .distributionPrice(modifyDistributionPrice)
                .alcohol(modifyAlcohol)
                .ingredient(modifyIngredient)
                .sweet(modifySweet)
                .sour(modifySour)
                .cool(modifyCool)
                .body(modifyBody)
                .balance(modifyBalance)
                .incense(modifyIncense)
                .throat(modifyThroat)
                .build();

        List<Integer> remove = new ArrayList<>();
        List<MultipartFile> files = new ArrayList<>();

        when(productRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(this.getProductOne());
        when(categoryRepository.findByIdAndDeletedAtIsNull(modifyCategoryLastId)).thenReturn(this.getModifyCategoryOne());
        when(makerRepository.findByIdAndDeletedAtIsNull(modifyMakerId)).thenReturn(this.getModifyMakerOne());
        Mockito.when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductDetailResponse productDetailResponse = adminProductService.modifyProduct(id, productModifyRequest, remove, files);

        // then
        assertThat(productDetailResponse.getId()).isEqualTo(id);
        assertThat(productDetailResponse.getCategoryLastId()).isEqualTo(modifyCategoryLastId);
        assertThat(productDetailResponse.getCategoryFirstName()).isEqualTo(modifyFirstName);
        assertThat(productDetailResponse.getCategoryLastName()).isEqualTo(modifyLastName);
        assertThat(productDetailResponse.getName()).isEqualTo(modifyName);
        assertThat(productDetailResponse.getMakerId()).isEqualTo(modifyMakerId);
        assertThat(productDetailResponse.getMakerName()).isEqualTo(modifyMakerName);
        assertThat(productDetailResponse.getPrice()).isEqualTo(modifyPrice);
        assertThat(productDetailResponse.getDistributionPrice()).isEqualTo(modifyDistributionPrice);
        assertThat(productDetailResponse.getQuantity()).isEqualTo(quantity);
        assertThat(productDetailResponse.getAlcohol()).isEqualTo(modifyAlcohol);
        assertThat(productDetailResponse.getIngredient()).isEqualTo(modifyIngredient);
        assertThat(productDetailResponse.getSweet()).isEqualTo(modifySweet);
        assertThat(productDetailResponse.getSour()).isEqualTo(modifySour);
        assertThat(productDetailResponse.getCool()).isEqualTo(modifyCool);
        assertThat(productDetailResponse.getBody()).isEqualTo(modifyBody);
        assertThat(productDetailResponse.getBalance()).isEqualTo(modifyBalance);
        assertThat(productDetailResponse.getIncense()).isEqualTo(modifyIncense);
        assertThat(productDetailResponse.getThroat()).isEqualTo(modifyThroat);
    }

    @Test
    @DisplayName("제품 수정 실패 - 찾을 수 없는 제품")
    public void modifyProductFailNotFoundTest() {
        // given
        Mockito.when(this.productRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.modifyProduct(id, any(), null, null);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제품 수정 실패 - 찾을 수 없는 카테고리 소분류")
    public void modifyProductFailCategoryNotFoundTest() {
        // given
        ProductModifyRequest productModifyRequest = ProductModifyRequest.builder()
                .categoryLastId(modifyCategoryLastId)
                .makerId(modifyMakerId)
                .build();

        when(productRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(this.getProductOne());
        when(categoryRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.modifyProduct(id, productModifyRequest, null, null);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_CATEGORY.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제품 수정 실패 - 찾을 수 없는 제조사")
    public void modifyProductFailMakerNotFoundTest() {
        // given
        ProductModifyRequest productModifyRequest = ProductModifyRequest.builder()
                .categoryLastId(categoryLastId)
                .makerId(makerId)
                .build();

        when(productRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(this.getProductOne());
        when(categoryRepository.findByIdAndDeletedAtIsNull(categoryLastId)).thenReturn(this.getCategoryOne());
        when(makerRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.modifyProduct(id, productModifyRequest, null, null);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_MAKER.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제품 삭제 성공")
    public void deleteProductTest() {
        // given
        when(productRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(this.getProductOne());
        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);

        // When
        adminProductService.deleteProduct(id);

        // then
        verify(productRepository).save(productCaptor.capture());
        Product savedProduct = productCaptor.getValue();
        assertThat(savedProduct.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("제품 삭제 실패 - 찾을 수 없는 제품")
    public void deleteProductFailNotFoundTest() {
        // given
        Mockito.when(this.productRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.deleteProduct(id);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("제품 삭제 실패 - 상품과 연결된 제품")
    public void deleteProductFailItemProductInUseTest() {
        // given
        Mockito.when(productRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(this.getProductOne());
        Mockito.when(itemProductRepository.existsByProductAndDeletedAtIsNull(any(Product.class))).thenReturn(true);

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.deleteProduct(id);
        });

        // then
        assertEquals(HttpResponse.Fail.PRODUCT_IN_USE.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.PRODUCT_IN_USE.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("재고 수량 조회 성공")
    public void t6() {
        // given
        when(productRepository.findById(id)).thenReturn(this.getProductOne());

        // When
        ProductQuantityResponse productQuantityResponse = adminProductService.getQuantity(id);

        // then
        assertThat(productQuantityResponse.getName()).isEqualTo(name);
        assertThat(productQuantityResponse.getQuantity()).isEqualTo(quantity);
    }

    @Test
    @DisplayName("재고 수량 조회 실패 - 찾을 수 없는 제품")
    public void t6_1() {
        // given
        Mockito.when(this.productRepository.findById(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.getQuantity(id);
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    @Test
    @DisplayName("재고 수량 수정 성공")
    public void t7() {
        // given
        ProductQuantityRequest request = ProductQuantityRequest.builder()
                .quantity(modifyQuantity)
                .build();

        when(productRepository.findByIdAndDeletedAtIsNull(id)).thenReturn(this.getProductOne());
        Mockito.when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ProductQuantityResponse productQuantityResponse = adminProductService.modifyQuantity(id, request);

        // then
        assertThat(productQuantityResponse.getName()).isEqualTo(name);
        assertThat(productQuantityResponse.getQuantity()).isEqualTo(modifyQuantity);
    }

    @Test
    @DisplayName("재고 수량 수정 실패 - 찾을 수 없는 제품")
    public void t7_1() {
        // given
        Mockito.when(this.productRepository.findByIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());

        // when
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            adminProductService.modifyQuantity(id, any());
        });

        // then
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getStatus(), exception.getStatus());
        assertEquals(HttpResponse.Fail.NOT_FOUND_PRODUCT.getMessage(), exception.getMessage());
    }

    private Page<Product> getProducts() {
        List<Product> list = List.of(this.getProductData());
        Pageable pageable = PageRequest.of(page, size);
        return new PageImpl<Product>(list, pageable, list.size());
    }

    private Optional<Product> getProductOne() {
        return Optional.of(this.getProductData());
    }

    private Optional<Maker> getMakerOne() {
        return Optional.of(this.getMakerData());
    }

    private Optional<Maker> getModifyMakerOne() {
        return Optional.of(this.getModifyMakerData());
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
                .id(id)
                .name(name)
                .price(price)
                .distributionPrice(distributionPrice)
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
                .maker(maker)
                .category(category)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }
}
