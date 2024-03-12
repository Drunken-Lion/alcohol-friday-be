package com.drunkenlion.alcoholfriday.domain.payment.util.converter;

import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentCardCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PaymentCardCodeConverter implements AttributeConverter<PaymentCardCode, String> {
    @Override
    public String convertToDatabaseColumn(PaymentCardCode attribute) {
        return attribute.getCardCode();
    }

    @Override
    public PaymentCardCode convertToEntityAttribute(String dbData) {
        return PaymentCardCode.ofCardCode(dbData);
    }
}
