package com.drunkenlion.alcoholfriday.domain.auth.dto;

import java.util.Map;

import com.drunkenlion.alcoholfriday.global.common.enumerated.ProviderType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class SocialUserInfo {
	protected Map<String, Object> attributes;

	public abstract String getName();

	public abstract String getEmail();

	public abstract String getNickname();

	public abstract ProviderType getProvider();
}
