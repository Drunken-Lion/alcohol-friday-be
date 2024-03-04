package com.drunkenlion.alcoholfriday.domain.category.entity;

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
@SuperBuilder(toBuilder = true)
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
