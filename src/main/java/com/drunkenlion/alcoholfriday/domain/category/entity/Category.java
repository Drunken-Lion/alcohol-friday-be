package com.drunkenlion.alcoholfriday.domain.category.entity;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    @Builder.Default
    private List<Product> products = new ArrayList<>();
}
