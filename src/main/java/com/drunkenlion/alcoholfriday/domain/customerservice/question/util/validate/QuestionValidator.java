package com.drunkenlion.alcoholfriday.domain.customerservice.question.util.validate;

import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public class QuestionValidator {
    public static void compareEntityIdToMemberId(Question entity, Member member) {
        if (!entity.getMember().getId().equals(member.getId())) {
            throw new BusinessException(Fail.FORBIDDEN);
        }
    }
}
