package com.drunkenlion.alcoholfriday.domain.item.entity;

import com.drunkenlion.alcoholfriday.domain.category.entity.Category;
import com.drunkenlion.alcoholfriday.domain.order.entity.OrderDetail;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.ItemType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@SuperBuilder(toBuilder = true)
@ToString
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "item")
public class Item extends BaseEntity {
    @Comment("상품 유형")
    @Column(name = "type", columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private ItemType type;

    @Comment("상품 이름")
    @Column(name = "name", columnDefinition = "VARCHAR(200)")
    private String name;

    @Comment("상품 가격")
    @Column(name = "price", columnDefinition = "DECIMAL(64, 3)")
    private BigDecimal price;

    @Comment("상품 설명")
    @Column(name = "info", columnDefinition = "MEDIUMTEXT")
    private String info;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", columnDefinition = "BIGINT", foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @OneToMany(mappedBy = "item")
    @Builder.Default
    private List<ItemProduct> itemProducts = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // 연관 관계 편의 메서드
    public void addCategory(Category category) {
        this.category = category;
        category.getItems().add(this);
    }
}
