package com.drunkenlion.alcoholfriday.domain.admin.customerservice.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class AdminQuestionValidator {

    public static void isAdmin(Member member) {
        if (!member.getRole().equals(MemberRole.ADMIN)) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }
}
