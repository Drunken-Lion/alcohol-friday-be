package com.drunkenlion.alcoholfriday.domain.admin.customerservice.question.util;

import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class AdminQuestionValidator {

    public static void hasRole(Member member) {
        if (!(member.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.SUPER_VISOR))) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }
}
