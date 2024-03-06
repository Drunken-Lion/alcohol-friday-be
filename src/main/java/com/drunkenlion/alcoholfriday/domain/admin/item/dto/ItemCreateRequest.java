package com.drunkenlion.alcoholfriday.domain.admin.item.dto;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "상품 등록 요청 항목")
public class ItemCreateRequest {
    @Schema(description = "상품의 판매 제품의 정보들")
    @NotEmpty
    private List<ItemProductInfo> itemProductInfos;

    @Schema(description = "카테고리 소분류 고유 아이디")
    private Long categoryLastId;

    @Schema(description = "상품명")
    private String name;

    @Schema(description = "가격")
    private BigDecimal price;

    @Schema(description = "정보")
    private String info;

    @Schema(description = "판매 유형")
    private ItemType type;

    public static Item toEntity(ItemCreateRequest request, Category category) {
        return Item.builder()
                .category(category)
                .name(request.getName())
                .price(request.getPrice())
                .info(request.getInfo())
                .type(request.getType())
                .build();
    }
}
