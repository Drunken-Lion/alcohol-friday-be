package com.drunkenlion.alcoholfriday.domain.item.dto;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class SearchItemRequest {
    @Setter
    private Integer size;
    private String keyword;
    private List<String> keywordType = new ArrayList<>();
}
