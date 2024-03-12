package com.drunkenlion.alcoholfriday.domain.payment.util.converter;

import com.drunkenlion.alcoholfriday.domain.payment.enumerated.PaymentProvider;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class PaymentProviderConverter implements AttributeConverter<PaymentProvider, String> {
    @Override
    public String convertToDatabaseColumn(PaymentProvider attribute) {
        return attribute.getPaymentProvider();
    }

    @Override
    public PaymentProvider convertToEntityAttribute(String dbData) {
        return PaymentProvider.ofPaymentProvider(dbData);
    }
}
