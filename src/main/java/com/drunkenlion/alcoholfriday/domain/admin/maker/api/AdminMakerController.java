package com.drunkenlion.alcoholfriday.domain.admin.maker.api;

import com.drunkenlion.alcoholfriday.domain.admin.maker.application.AdminMakerService;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerDetailResponse;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerListResponse;
import com.drunkenlion.alcoholfriday.domain.admin.maker.dto.MakerRequest;
import com.drunkenlion.alcoholfriday.global.common.response.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin/makers")
@Tag(name = "v1-admin-maker", description = "관리자 제조사 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class AdminMakerController {
    private final AdminMakerService adminMakerService;

    @Operation(summary = "전체 제조사 조회", description = "관리자 권한에 대한 전체 제조사 조회")
    @GetMapping
    public ResponseEntity<PageResponse<MakerListResponse>> getMakers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size
    ) {
        PageResponse<MakerListResponse> pageResponse = PageResponse.of(this.adminMakerService.getMakers(page, size));
        return ResponseEntity.ok().body(pageResponse);
    }

    @Operation(summary = "제조사 상세 조회", description = "관리자 권한에 대한 제조사 상세 조회")
    @GetMapping("{id}")
    public ResponseEntity<MakerDetailResponse> getMaker(
            @PathVariable("id") Long id
    ) {
        MakerDetailResponse makerDetailResponse = adminMakerService.getMaker(id);
        return ResponseEntity.ok().body(makerDetailResponse);
    }

    @Operation(summary = "제조사 등록", description = "관리자 권한에 대한 제조사 등록")
    @PostMapping
    public ResponseEntity<MakerDetailResponse> createMaker(
            @Valid @RequestBody MakerRequest makerRequest
    ) {
        MakerDetailResponse makerDetailResponse = adminMakerService.createMaker(makerRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(makerDetailResponse.getId())
                .toUri();

        return ResponseEntity.created(location).body(makerDetailResponse);
    }

    @Operation(summary = "제조사 수정", description = "관리자 권한에 대한 제조사 수정")
    @PutMapping("{id}")
    public ResponseEntity<MakerDetailResponse> modifyMaker(
            @PathVariable("id") Long id,
            @Valid @RequestBody MakerRequest makerRequest
    ) {
        MakerDetailResponse makerDetailResponse = adminMakerService.modifyMaker(id, makerRequest);
        return ResponseEntity.ok().body(makerDetailResponse);
    }

    @Operation(summary = "제조사 삭제", description = "관리자 권한에 대한 제조사 삭제")
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteMaker(
            @PathVariable("id") Long id
    ) {
        adminMakerService.deleteMaker(id);
        return ResponseEntity.noContent().build();
    }
}
