package com.drunkenlion.alcoholfriday.global.common.enumerated;

import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.entity.Question;
import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;

public enum EntityTypeV2 {

    QUESTION("question", Question.builder().build()),
    ANSWER("answer", Answer.builder().build()),
    ITEM("item", Item.builder().build()),
    PRODUCT("product", Product.builder().build()),
    MEMBER("member", Member.builder().build()),
    NOTICE("notice", Notice.builder().build())
    ;

    private final String entityType;
    private final BaseEntity entityObj;

    EntityTypeV2(String entityType, BaseEntity entityObj) {
        this.entityType = entityType;
        this.entityObj = entityObj;
    }

    public static String getEntityType(BaseEntity entityObj) {
        for (EntityTypeV2 entity : EntityTypeV2.values()) {
            if (entityObj.getClass().isInstance(entity.entityObj)) {
                return entity.entityType;
            }
        }
        throw new BusinessException(HttpResponse.Fail.NOT_FOUND);
    }
}
