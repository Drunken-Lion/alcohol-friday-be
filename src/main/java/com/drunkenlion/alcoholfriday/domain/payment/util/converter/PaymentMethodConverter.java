package com.drunkenlion.alcoholfriday.domain.payment.util.converter;

import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentMethod;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {
    @Override
    public String convertToDatabaseColumn(PaymentMethod attribute) {
        return attribute.getMethod();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String dbData) {
        return PaymentMethod.ofMethod(dbData);
    }
}
