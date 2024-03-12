package com.drunkenlion.alcoholfriday.domain.payment.util.converter;

import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentCardType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PaymentCardTypeConverter implements AttributeConverter<PaymentCardType, String> {
    @Override
    public String convertToDatabaseColumn(PaymentCardType attribute) {
        return attribute.getCardType();
    }

    @Override
    public PaymentCardType convertToEntityAttribute(String dbData) {
        return PaymentCardType.ofCardType(dbData);
    }
}
