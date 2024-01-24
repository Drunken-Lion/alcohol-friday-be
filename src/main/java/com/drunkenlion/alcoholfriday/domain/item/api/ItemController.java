package com.drunkenlion.alcoholfriday.domain.item.api;

import com.drunkenlion.alcoholfriday.domain.item.application.ItemService;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemRequest;
import com.drunkenlion.alcoholfriday.domain.item.dto.SearchItemResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Page<SearchItemResponse>> search(
            @RequestBody SearchItemRequest searchItemRequest
    ) {
        if (searchItemRequest.getSize() < 1) searchItemRequest.setSize(10);
        Page<SearchItemResponse> search = this.itemService.search(searchItemRequest);
        return ResponseEntity.ok().body(search);
    }
}
