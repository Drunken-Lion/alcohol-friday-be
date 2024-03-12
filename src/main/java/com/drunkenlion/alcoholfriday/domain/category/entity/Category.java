package com.drunkenlion.alcoholfriday.domain.category.entity;

import com.drunkenlion.alcoholfriday.domain.item.entity.Item;
import com.drunkenlion.alcoholfriday.domain.product.entity.Product;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category")
public class Category extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_class_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
	private CategoryClass categoryClass;

	@Comment("소분류")
	@Column(name = "last_name", columnDefinition = "VARCHAR(50)")
	private String lastName;

	@OneToMany(mappedBy = "category")
	@Builder.Default
	private List<Item> items = new ArrayList<>();

	@OneToMany(mappedBy = "category")
	@Builder.Default
	private List<Product> products = new ArrayList<>();

	// 연관 관계 편의 메서드
	public void addCategoryClass(CategoryClass categoryClass) {
		this.categoryClass = categoryClass;
		categoryClass.getCategories().add(this);
	}
}
