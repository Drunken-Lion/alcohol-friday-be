package com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity;

import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.enumerated.NoticeStatus;
import com.drunkenlion.alcoholfriday.domain.admin.customerservice.notice.util.converter.NoticeStatusConverter;
import com.drunkenlion.alcoholfriday.domain.member.entity.Member;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "notice")
public class Notice extends BaseEntity {
    @Comment("공지사항 작성자")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @Comment("공지사항 제목")
    @Column(name = "title", columnDefinition = "VARCHAR(200)")
    private String title;

    @Comment("공지사항 내용")
    @Column(name = "content", columnDefinition = "MEDIUMTEXT")
    private String content;

    @Comment("작성 상태")
    @Column(name = "status", columnDefinition = "VARCHAR(20)")
    @Convert(converter = NoticeStatusConverter.class)
    private NoticeStatus status;

    public void updateNotice(String title, String content) {
        this.title = title;
        this.content = content;
        this.status = NoticeStatus.PUBLISHED;
    }
}
