package com.drunkenlion.alcoholfriday.domain.member.util;

import com.drunkenlion.alcoholfriday.global.common.enumerated.MemberRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * @Enumerated 어노테이션 사용보다 성능면에서 우수하여 채용
 * 참고자료 : https://techblog.woowahan.com/2600/
 */
@Converter
public class MemberRoleConverter implements AttributeConverter<MemberRole, String> {

    @Override
    public String convertToDatabaseColumn(MemberRole attribute) {
        return attribute.getRole();
    }

    @Override
    public MemberRole convertToEntityAttribute(String dbData) {
        return null;
    }
}
