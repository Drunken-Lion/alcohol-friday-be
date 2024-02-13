package com.drunkenlion.alcoholfriday.domain.auth.dto;

import java.util.Map;

import com.drunkenlion.alcoholfriday.global.common.enumerated.ProviderType;

import lombok.Builder;

public class KakaoUserInfo extends SocialUserInfo {
	private Map<String, Object> account;
	private Map<String, Object> profile;

	@Builder
	public KakaoUserInfo(Map<String, Object> attributes) {
		super(attributes);
		this.account = (Map<String, Object>)attributes.get("kakao_account");
		this.profile = (Map<String, Object>)account.get("profile");
	}

	@Override
	public String getName() {
		return (String)account.get("name");
	}

	@Override
	public String getEmail() {
		return (String)account.get("email");
	}

	@Override
	public String getNickname() {
		return (String)profile.get("nickname");
	}

	@Override
	public ProviderType getProvider() {
		return ProviderType.KAKAO;
	}
}
