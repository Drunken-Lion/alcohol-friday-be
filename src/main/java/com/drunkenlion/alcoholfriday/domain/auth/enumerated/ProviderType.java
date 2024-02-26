package com.drunkenlion.alcoholfriday.domain.auth.enumerated;

import java.util.Arrays;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

@Getter
public enum ProviderType {
    KAKAO("kakao");

    private final String providerName;

    ProviderType(String providerName) {
        this.providerName = providerName;
    }

    public static ProviderType ofProvider(String providerName) {
        return Arrays.stream(ProviderType.values())
                .filter(value -> value.providerName.equals(providerName))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PROVIDER)
                        .build());
    }
}
