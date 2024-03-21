package com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.util.converter;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NoticeStatusConverter implements AttributeConverter<NoticeStatus, String> {

    @Override
    public String convertToDatabaseColumn(NoticeStatus attribute) {
        return attribute.getStatusNumber();
    }

    @Override
    public NoticeStatus convertToEntityAttribute(String dbData) {
        return NoticeStatus.byStatusNumber(dbData);
    }

}


