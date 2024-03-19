package com.drunkenlion.alcoholfriday.domain.cart.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "장바구니에서 삭제할 상품")
public class DeleteCartRequest {
    @Schema(description = "장바구니에서 삭제할 상품 고유 번호")
    private Long itemId;

    public static DeleteCartRequest of(Long itemId) {
        return DeleteCartRequest.builder()
                .itemId(itemId)
                .build();
    }
}
