package com.drunkenlion.alcoholfriday.domain.admin.item.api;

import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.item.dto.ItemRequest;
import com.drunkenlion.alcoholfriday.domain.admin.item.application.AdminItemService;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
@Tag(name = "v1-admin-item", description = "관리자 상품 관리 API")
public class AdminItemController {
    private final AdminItemService adminItemService;

    @Operation(summary = "전체 상품 조회", description = "관리자 권한에 대한 전체 상품 조회")
    @GetMapping(value = "items")
    public ResponseEntity<PageResponse<ItemListResponse>> getItems(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<ItemListResponse> pageResponse = PageResponse.of(this.adminItemService.getItems(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "상품 상세 상품", description = "관리자 권한에 대한 상품 상세 조회")
    @GetMapping(value = "items/{id}")
    public ResponseEntity<ItemDetailResponse> getItem(
            @PathVariable("id") Long id
    ) {
        ItemDetailResponse itemDetailResponse = adminItemService.getItem(id);
        return ResponseEntity.ok().body(itemDetailResponse);
    }

    @Operation(summary = "상품 등록", description = "관리자 권한에 대한 상품 등록")
    @PostMapping(value = "items")
    public ResponseEntity<ItemDetailResponse> createItem(
            @Valid @RequestBody ItemRequest itemRequest
    ) {
        ItemDetailResponse itemDetailResponse = adminItemService.createItem(itemRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(itemDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(itemDetailResponse);
    }

    @Operation(summary = "상품 수정", description = "관리자 권한에 대한 상품 수정")
    @PutMapping(value = "items/{id}")
    public ResponseEntity<ItemDetailResponse> modifyItem(
            @PathVariable("id") Long id,
            @Valid @RequestBody ItemRequest itemRequest
    ) {
        ItemDetailResponse itemDetailResponse = adminItemService.modifyItem(id, itemRequest);
        return ResponseEntity.ok().body(itemDetailResponse);
    }

    @Operation(summary = "상품 삭제", description = "관리자 권한에 대한 상품 삭제")
    @DeleteMapping(value = "items/{id}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("id") Long id
    ) {
        adminItemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}
