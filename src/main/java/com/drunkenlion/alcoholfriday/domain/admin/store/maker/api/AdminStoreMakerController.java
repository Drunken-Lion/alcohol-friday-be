package com.drunkenlion.alcoholfriday.domain.admin.store.maker.api;

import com.drunkenlion.alcoholfriday.domain.admin.store.maker.application.AdminStoreMakerService;
import com.drunkenlion.alcoholfriday.domain.admin.store.maker.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.maker.dto.MakerListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.store.maker.dto.MakerRequest;
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
@RequestMapping("/v1/admin/store")
@Tag(name = "v1-admin-store-maker", description = "관리자 스토어 제조사 관리에 대한 API")
public class AdminStoreMakerController {
    private final AdminStoreMakerService adminStoreMakerService;

    @Operation(summary = "전체 제조사 조회", description = "관리자 권한에 대한 전체 제조사 조회")
    @GetMapping(value = "makers")
    public ResponseEntity<PageResponse<MakerListResponse>> getMakers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<MakerListResponse> pageResponse = PageResponse.of(this.adminStoreMakerService.getMakers(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "제조사 상세 조회", description = "관리자 권한에 대한 제조사 상세 조회")
    @GetMapping(value = "makers/{id}")
    public ResponseEntity<MakerDetailResponse> getMaker(
            @PathVariable("id") Long id
    ) {
        MakerDetailResponse makerDetailResponse = adminStoreMakerService.getMaker(id);
        return ResponseEntity.ok().body(makerDetailResponse);
    }

    @Operation(summary = "제조사 등록", description = "관리자 권한에 대한 제조사 등록")
    @PostMapping(value = "makers")
    public ResponseEntity<MakerDetailResponse> createMaker(
            @Valid @RequestBody MakerRequest makerRequest
    ) {
        MakerDetailResponse makerDetailResponse = adminStoreMakerService.createMaker(makerRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(makerDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(makerDetailResponse);
    }

    @Operation(summary = "제조사 수정", description = "관리자 권한에 대한 제조사 수정")
    @PutMapping(value = "makers/{id}")
    public ResponseEntity<MakerDetailResponse> modifyMaker(
            @PathVariable("id") Long id,
            @Valid @RequestBody MakerRequest makerRequest
    ) {
        MakerDetailResponse makerDetailResponse = adminStoreMakerService.modifyMaker(id, makerRequest);
        return ResponseEntity.ok().body(makerDetailResponse);
    }

    @Operation(summary = "제조사 삭제", description = "관리자 권한에 대한 제조사 삭제")
    @DeleteMapping(value = "makers/{id}")
    public ResponseEntity<Void> deleteMaker(
            @PathVariable("id") Long id
    ) {
        adminStoreMakerService.deleteMaker(id);
        return ResponseEntity.noContent().build();
    }
}
