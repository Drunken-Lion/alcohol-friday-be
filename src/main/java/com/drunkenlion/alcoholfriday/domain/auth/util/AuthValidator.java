package com.drunkenlion.alcoholfriday.domain.auth.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

import java.util.Optional;

public class AuthValidator {
    public static void validateCertifyAt(Member member) {
        if (member.getRole().equals(MemberRole.MEMBER)
                && member.getCertifyAt() != null) {
            throw new BusinessException(HttpResponse.Fail.ADULT_ALREADY_VERIFIED);
        }
    }

    public static void validRequestToken(String token) {
        Optional.ofNullable(token)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.BAD_REQUEST));
    }
}
