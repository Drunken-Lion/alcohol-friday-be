package com.drunkenlion.alcoholfriday.domain.category.entity;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category extends BaseEntity {
    @Column(name = "first_name", columnDefinition = "VARCHAR(50)")
    @Comment("대분류")
    private String firstName;

    @Column(name = "middle_name", columnDefinition = "VARCHAR(50)")
    @Comment("중분류")
    private String middleName;

    @Column(name = "last_name", columnDefinition = "VARCHAR(50)")
    @Comment("소분류")
    private String lastName;
}
