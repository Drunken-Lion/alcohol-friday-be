package com.drunkenlion.alcoholfriday.domain.address.util;

import com.drunkenlion.alcoholfriday.domain.address.entity.Address;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class AddressValidator {
    public static void validateAuthority(Address address, Member member) {
        if (!address.getMember().getId().equals(member.getId())) {
            throw new BusinessException(HttpResponse.Fail.FORBIDDEN);
        }
    }
}
