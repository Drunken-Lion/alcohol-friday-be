package com.drunkenlion.alcoholfriday.domain.admin.restaurant.cart.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class RestaurantOrderCartValidator {
    public static void checkedMemberRoleIsOwner(Member member) {
        if (!member.getRole().equals(MemberRole.OWNER)) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }
    public static void checkedQuantity(Product product, Long inQuantity) {
        if (product.getQuantity() < inQuantity) {
            throw new BusinessException(Fail.OUT_OF_PRODUCT_STOCK);
        }
    }
    public static void minimumQuantity(Long inQuantity) {
        if (inQuantity < 0) {
            throw new BusinessException(Fail.INVALID_INPUT_PRODUCT_QUANTITY);
        }
    }
}
