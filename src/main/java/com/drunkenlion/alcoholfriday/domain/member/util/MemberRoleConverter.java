package com.drunkenlion.alcoholfriday.domain.member.util;

import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class MemberRoleConverter implements AttributeConverter<MemberRole, String> {
	@Override
	public String convertToDatabaseColumn(MemberRole attribute) {
		return attribute.getRole();
	}

	@Override
	public MemberRole convertToEntityAttribute(String dbData) {
		return MemberRole.ofRole(dbData);
	}
}
