package com.drunkenlion.alcoholfriday.global.ncp.entity;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class NcpFile extends BaseEntity {

    @Type(JsonType.class)
    @Column(name = "s3_files", columnDefinition ="json")
    private List<Map<String, Object>> s3Files;

    @ColumnDefault("0")
    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "entity_type" , length = 20)
    private String entityType;

}
