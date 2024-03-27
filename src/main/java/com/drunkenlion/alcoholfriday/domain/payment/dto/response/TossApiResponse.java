package com.drunkenlion.alcoholfriday.domain.payment.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.json.simple.JSONObject;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Schema(description = "최종 결제 취소 후 토스페이먼츠에서 주는 결제 취소 응답 값")
public class TossApiResponse {
    @Schema(description = "결제 취소 성공/실패 여부")
    boolean isSuccess;

    @Schema(description = "결제 취소 성공/실패 코드")
    int code;

    @Schema(description = "결제 취소 성공/실패 응답 코드")
    JSONObject jsonObject;
}
