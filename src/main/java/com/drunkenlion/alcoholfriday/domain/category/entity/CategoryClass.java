package com.drunkenlion.alcoholfriday.domain.category.entity;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Comment;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "category_class")
public class CategoryClass extends BaseEntity {
	@Column(name = "first_name", columnDefinition = "VARCHAR(50)")
	@Comment("대분류")
	private String firstName;

	@OneToMany(mappedBy = "categoryClass")
	@Builder.Default
	private List<Category> categories = new ArrayList<>();
}
