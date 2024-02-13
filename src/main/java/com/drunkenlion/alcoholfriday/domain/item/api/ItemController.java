package com.drunkenlion.alcoholfriday.domain.item.api;

import com.drunkenlion.alcoholfriday.domain.item.application.ItemService;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "keywordType") @Schema(example = "type,name") String keywordType
    ) {
        List<String> parseType = List.of(keywordType.split(","));
        PageResponse<SearchItemResponse> pageResponse = PageResponse.of(this.itemService.search(size, keyword, parseType));
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
}
