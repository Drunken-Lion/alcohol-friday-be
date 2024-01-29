package com.drunkenlion.alcoholfriday.domain.item.api;

import com.drunkenlion.alcoholfriday.domain.item.application.ItemService;
import com.drunkenlion.alcoholfriday.domain.item.dto.FindItemResponse;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemRequest;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<PageResponse<SearchItemResponse>> search(
            @RequestBody SearchItemRequest searchItemRequest
    ) {
        if (searchItemRequest.getSize() < 1) searchItemRequest.setSize(10);
        PageResponse<SearchItemResponse> pageResponse = PageResponse.of(this.itemService.search(searchItemRequest));
        return ResponseEntity.ok().body(pageResponse);
    }

    @GetMapping("{id}")
    public ResponseEntity<FindItemResponse> get(
            @PathVariable("id") Long id
    ) {
        FindItemResponse findItemResponse = this.itemService.get(id);
        return ResponseEntity.ok().body(findItemResponse);
    }
}
