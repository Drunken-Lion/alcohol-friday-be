package com.drunkenlion.alcoholfriday.domain.auth.enumerated;

import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ProviderType {
    KAKAO("kakao", "1");

    private final String providerName;
    private final String providerNumber;

    ProviderType(String providerName, String providerNumber) {
        this.providerName = providerName;
        this.providerNumber = providerNumber;
    }

    public static ProviderType byProviderName(String providerName) {
        return Arrays.stream(ProviderType.values())
                .filter(value -> value.getProviderName().equals(providerName))
                .findFirst()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PROVIDER)
                        .build());
    }

    public static ProviderType byProviderNumber(String providerNumber) {
        return Arrays.stream(ProviderType.values())
                .filter(value -> value.getProviderNumber().equals(providerNumber))
                .findAny()
                .orElseThrow(() -> BusinessException.builder()
                        .response(HttpResponse.Fail.NOT_FOUND_PROVIDER_NUMBER)
                        .build());
    }
}
