package com.drunkenlion.alcoholfriday.domain.admin.item.application;

import com.drunkenlion.alcoholfriday.domain.admin.item.dto.*;
import com.drunkenlion.alcoholfriday.domain.cart.dao.CartDetailRepository;
import com.drunkenlion.alcoholfriday.domain.cart.entity.CartDetail;
import com.drunkenlion.alcoholfriday.domain.category.dao.CategoryRepository;
import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemProductRepository;
import com.drunkenlion.alcoholfriday.domain.item.dao.ItemRepository;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.item.entity.ItemProduct;
import com.drunkenlion.alcoholfriday.domain.product.dao.ProductRepository;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.application.FileService;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminItemServiceImpl implements AdminItemService {
    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final ItemProductRepository itemProductRepository;
    private final CartDetailRepository cartDetailRepository;
    private final FileService fileService;

    @Override
    public Page<ItemListResponse> getItems(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Item> items = itemRepository.findAll(pageable);

        return items.map(ItemListResponse::of);
    }

    @Override
    public ItemDetailResponse getItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ITEM)
                        .build());

        NcpFileResponse file = fileService.findAll(item);

        return ItemDetailResponse.of(item, file);
    }

    @Override
    @Transactional
    public ItemDetailResponse createItem(ItemCreateRequest itemCreateRequest, List<MultipartFile> files) {
        Category category = categoryRepository.findByIdAndDeletedAtIsNull(itemCreateRequest.getCategoryLastId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        Item item = ItemCreateRequest.toEntity(itemCreateRequest, category);
        itemRepository.save(item);

        List<ItemProduct> itemProducts = new ArrayList<>();
        for (ItemProductInfo itemProductInfo : itemCreateRequest.getItemProductInfos()) {
            Product product = productRepository.findByIdAndDeletedAtIsNull(itemProductInfo.getProductId())
                    .orElseThrow(() -> BusinessException.builder()
                            .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                            .build());
            ItemProduct itemProduct = ItemProductInfo.toEntity(itemProductInfo, item, product);
            itemProducts.add(itemProduct);
        }

        item = item.toBuilder()
                .itemProducts(itemProducts)
                .build();

        itemProductRepository.saveAll(itemProducts);

        NcpFileResponse file = fileService.saveFiles(item, files);

        return ItemDetailResponse.of(item, file);
    }

    @Override
    @Transactional
    public ItemDetailResponse modifyItem(Long id, ItemModifyRequest itemModifyRequest, List<Integer> remove, List<MultipartFile> files) {
        Item item = itemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ITEM)
                        .build());

        Category category = categoryRepository.findByIdAndDeletedAtIsNull(itemModifyRequest.getCategoryLastId())
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_CATEGORY)
                        .build());

        // 이전 itemProduct 삭제 처리
        List<ItemProduct> itemProducts = itemProductRepository.findByItemAndDeletedAtIsNull(item);
        if (!itemProducts.isEmpty()) {
            itemProducts = itemProducts.stream()
                    .map(itemProduct -> itemProduct.toBuilder()
                            .deletedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            itemProductRepository.saveAll(itemProducts);
        }

        // 새 itemProduct 등록 처리
        List<ItemProduct> newItemProducts = new ArrayList<>();
        for (ItemProductInfo itemProductInfo : itemModifyRequest.getItemProductInfos()) {
            Product product = productRepository.findByIdAndDeletedAtIsNull(itemProductInfo.getProductId())
                    .orElseThrow(() -> BusinessException.builder()
                            .response(HttpResponse.Fail.NOT_FOUND_PRODUCT)
                            .build());
            ItemProduct itemProduct = ItemProductInfo.toEntity(itemProductInfo, item, product);
            newItemProducts.add(itemProduct);
        }

        item = item.toBuilder()
                .type(itemModifyRequest.getType())
                .name(itemModifyRequest.getName())
                .price(itemModifyRequest.getPrice())
                .info(itemModifyRequest.getInfo())
                .category(category)
                .itemProducts(newItemProducts)
                .build();

        itemProductRepository.saveAll(newItemProducts);
        itemRepository.save(item);

        NcpFileResponse file = fileService.updateFiles(item, remove, files);

        return ItemDetailResponse.of(item, file);
    }

    @Override
    @Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_ITEM)
                        .build());

        // item 와 관계가 있는 itemProduct 삭제 처리
        List<ItemProduct> itemProducts = itemProductRepository.findByItemAndDeletedAtIsNull(item);
        if (!itemProducts.isEmpty()) {
            itemProducts = itemProducts.stream()
                    .map(itemProduct -> itemProduct.toBuilder()
                            .deletedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            itemProductRepository.saveAll(itemProducts);
        }

        // item 와 관계가 있는 cartDetail 삭제 처리
        List<CartDetail> cartDetails = cartDetailRepository.findByItemAndDeletedAtIsNull(item);
        if (!cartDetails.isEmpty()) {
            cartDetails = cartDetails.stream()
                    .map(cartDetail -> cartDetail.toBuilder()
                            .deletedAt(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());

            cartDetailRepository.saveAll(cartDetails);
        }

        item = item.toBuilder()
                .deletedAt(LocalDateTime.now())
                .build();

        itemRepository.save(item);
    }
}
