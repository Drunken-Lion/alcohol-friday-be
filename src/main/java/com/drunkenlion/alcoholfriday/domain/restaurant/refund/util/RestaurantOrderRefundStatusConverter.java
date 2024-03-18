package com.drunkenlion.alcoholfriday.domain.restaurant.refund.util;

import com.drunkenlion.alcoholfriday.domain.restaurant.refund.enumerated.RestaurantOrderRefundStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RestaurantOrderRefundStatusConverter implements AttributeConverter<RestaurantOrderRefundStatus, String> {
    @Override
    public String convertToDatabaseColumn(RestaurantOrderRefundStatus attribute) {
        return attribute.getRoleValue();
    }

    @Override
    public RestaurantOrderRefundStatus convertToEntityAttribute(String dbData) {
        return RestaurantOrderRefundStatus.byRoleValue(dbData);
    }
}
