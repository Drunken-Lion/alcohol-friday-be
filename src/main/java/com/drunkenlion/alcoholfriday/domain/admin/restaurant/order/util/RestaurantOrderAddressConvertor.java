package com.drunkenlion.alcoholfriday.domain.admin.restaurant.order.util;

import com.drunkenlion.alcoholfriday.domain.restaurant.restaurant.entity.Restaurant;
import java.util.Optional;

public class RestaurantOrderAddressConvertor {
    public static String addressFormatter(Restaurant restaurant) {
        String address = Optional.ofNullable(restaurant.getAddress()).orElse("");
        String addressDetail = Optional.ofNullable(restaurant.getAddressDetail()).orElse("");
        String postcode = " [" + restaurant.getPostcode() + "]";

        if (restaurant.getPostcode() == null || restaurant.getPostcode().isEmpty()) {
            postcode = "";
        }

        return String.format("%s %s%s", address, addressDetail, postcode);
    }
}
