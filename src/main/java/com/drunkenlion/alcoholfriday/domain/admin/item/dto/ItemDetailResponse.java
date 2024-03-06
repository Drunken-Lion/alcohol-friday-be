package com.drunkenlion.alcoholfriday.domain.admin.item.dto;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "상품 상세 조회 항목")
public class ItemDetailResponse {
    @Schema(description = "고유 아이디")
    private Long id;

    @Schema(description = "상품의 판매 제품들")
    private List<ItemProductInfo> itemProductInfos;

    @Schema(description = "카테고리 소분류 고유 아이디")
    private Long categoryLastId;

    @Schema(description = "카테고리 대분류 이름")
    private String categoryFirstName;

    @Schema(description = "카테고리 소분류 이름")
    private String categoryLastName;

    @Schema(description = "상품명")
    private String name;

    @Schema(description = "가격")
    private BigDecimal price;

    @Schema(description = "정보")
    private String info;

    @Schema(description = "판매 유형")
    private ItemType type;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "마지막 수정일시")
    private LocalDateTime updatedAt;

    @Schema(description = "삭제일시")
    private LocalDateTime deletedAt;

    @Schema(description = "상품 이미지")
    private NcpFileResponse itemFiles;

    public static ItemDetailResponse of(Item item, NcpFileResponse file) {
        List<ItemProductInfo> itemProductInfos = item.getItemProducts().stream()
                .map(ItemProductInfo::of)
                .collect(Collectors.toList());

        return ItemDetailResponse.builder()
                .id(item.getId())
                .itemProductInfos(itemProductInfos)
                .categoryLastId(item.getCategory().getId())
                .categoryFirstName(item.getCategory().getCategoryClass().getFirstName())
                .categoryLastName(item.getCategory().getLastName())
                .name(item.getName())
                .price(item.getPrice())
                .info(item.getInfo())
                .type(item.getType())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .deletedAt(item.getDeletedAt())
                .itemFiles(file)
                .build();
    }
}
