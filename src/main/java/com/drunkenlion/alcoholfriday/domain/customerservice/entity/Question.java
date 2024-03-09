package com.drunkenlion.alcoholfriday.domain.customerservice.entity;

import com.drunkenlion.alcoholfriday.domain.customerservice.enumerated.QuestionStatus;
import com.drunkenlion.alcoholfriday.domain.customerservice.util.QuestionStatusConverter;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.Comment;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Entity
public class Question extends BaseEntity {
    @Comment("문의사항 작성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Comment("문의사항 제목")
    @Column(length = 200)
    private String title;

    @Comment("문의사항 내용")
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @Comment("답변 여부")
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
