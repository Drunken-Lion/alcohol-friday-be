package com.drunkenlion.alcoholfriday.domain.payment.util.converter;

import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentOwnerType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PaymentOwnerTypeConverter implements AttributeConverter<PaymentOwnerType, String> {
    @Override
    public String convertToDatabaseColumn(PaymentOwnerType attribute) {
        return attribute.getOwnerType();
    }

    @Override
    public PaymentOwnerType convertToEntityAttribute(String dbData) {
        return PaymentOwnerType.ofOwnerType(dbData);
    }
}
