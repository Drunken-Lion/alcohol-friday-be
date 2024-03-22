package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.util;

import com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.enumerated.RestaurantOrderStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class RestaurantOrderStatusConverter implements AttributeConverter<RestaurantOrderStatus, String> {
    @Override
    public String convertToDatabaseColumn(RestaurantOrderStatus attribute) {
        return attribute.getNumber();
    }

    @Override
    public RestaurantOrderStatus convertToEntityAttribute(String dbData) {
        return RestaurantOrderStatus.byNumber(dbData);
    }
}
