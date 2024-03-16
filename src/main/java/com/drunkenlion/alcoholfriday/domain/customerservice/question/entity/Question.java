package com.drunkenlion.alcoholfriday.domain.customerservice.question.entity;

import com.drunkenlion.alcoholfriday.domain.customerservice.answer.entity.Answer;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.question.util.converter.QuestionStatusConverter;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
@Table(name = "question")
public class Question extends BaseEntity {
    @Comment("문의사항 작성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Comment("문의사항 제목")
    @Column(name = "title", columnDefinition = "VARCHAR(200)")
    private String title;

    @Comment("문의사항 내용")
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    @Comment("답변 여부")
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Convert(converter = QuestionStatusConverter.class)
    private QuestionStatus status;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    private List<Answer> answers = new ArrayList<>();

    public void addMember(Member member) {
        this.member = member;
        member.getQuestions().add(this);
    }

    public void updateQuestion(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void updateStatus(QuestionStatus status) {
        this.status = status;
    }
}
