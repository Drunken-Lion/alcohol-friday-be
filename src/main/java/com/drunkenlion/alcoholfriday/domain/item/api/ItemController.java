package com.drunkenlion.alcoholfriday.domain.item.api;

import com.drunkenlion.alcoholfriday.domain.item.application.ItemService;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.ItemReviewResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/items")
@Tag(name = "v1-item", description = "상품(실제로 고객에게 판매)에 대한 API")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    @Operation(summary = "검색어로 전체 상품 조회", description = "검색어와 검색유형에 따라 전체 상품을 여러개 조회한다.")
    public ResponseEntity<PageResponse<SearchItemResponse>> search(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "12") int size,
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "categories") @Schema(example = "탁주/막걸리 또는 과실주/와인") String categories
    ) {
        List<String> parseType = categories == null || categories.isBlank()
                ? Collections.emptyList()
                : List.of(categories.split(","));

        PageResponse<SearchItemResponse> pageResponse = PageResponse.of(this.itemService.search(page, size, keyword, parseType));
        return ResponseEntity.ok().body(pageResponse);
    }

    @GetMapping("{id}")
    @Operation(summary = "상품 상세 조회", description = "상품의 식별자를 통해 상품을 하나만 조회한다.")
    public ResponseEntity<FindItemResponse> get(
            @PathVariable("id") Long id
    ) {
        FindItemResponse findItemResponse = this.itemService.get(id);
        return ResponseEntity.ok().body(findItemResponse);
    }

    @GetMapping("{id}/reviews")
    @Operation(summary = "상품 리뷰 조회")
    public ResponseEntity<PageResponse<ItemReviewResponse>> getReview(@PathVariable("id") Long id,
                                       @RequestParam(name = "page", defaultValue = "0") int page,
                                       @RequestParam(name = "size", defaultValue = "10") int size) {
        PageResponse<ItemReviewResponse> response = PageResponse.of(itemService.getReviews(id, page, size));
        return ResponseEntity.ok(response);
    }
}
