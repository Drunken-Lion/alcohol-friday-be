package com.drunkenlion.alcoholfriday.domain.auth.enumerated;

import java.util.Arrays;

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
                .orElseThrow(() -> new IllegalArgumentException(String.format("%s 은(는) 존재하지 않는 제공처 입니다.", providerName)));
    }
}
