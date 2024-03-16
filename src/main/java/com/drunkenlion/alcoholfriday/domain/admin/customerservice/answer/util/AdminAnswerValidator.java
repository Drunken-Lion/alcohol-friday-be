package com.drunkenlion.alcoholfriday.domain.admin.customerservice.answer.util;

import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.member.enumerated.MemberRole;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class AdminAnswerValidator {
    public static void hasRole(Member member) {
        if (!(member.getRole().equals(MemberRole.ADMIN) || member.getRole().equals(MemberRole.SUPER_VISOR))) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }

    public static void isDeleted(Question entity) {
        if (entity.getDeletedAt() != null) {
            throw new BusinessException(Fail.DELETED_QUESTION);
        }
    }

    public static void isDeleted(Answer entity) {
        if (entity.getDeletedAt() != null) {
            throw new BusinessException(Fail.DELETED_ANSWER);
        }
    }
}
