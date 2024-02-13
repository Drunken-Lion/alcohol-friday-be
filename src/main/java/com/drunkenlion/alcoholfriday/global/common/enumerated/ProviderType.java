package com.drunkenlion.alcoholfriday.global.common.enumerated;

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
			.orElseThrow(() -> new IllegalArgumentException(String.format("제공처에 %s가 존재하지 않습니다.", providerName)));
	}
}
