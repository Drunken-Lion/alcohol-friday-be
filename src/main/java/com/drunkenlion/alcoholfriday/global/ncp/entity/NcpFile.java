package com.drunkenlion.alcoholfriday.global.ncp.entity;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ncp_file")
public class NcpFile extends BaseEntity {
	@Type(JsonType.class)
	@Column(name = "s3_files", columnDefinition = "JSON")
	@Comment("file 정보가 저장된 json")
	private List<Map<String, Object>> s3Files;

	@Column(name = "entity_id", columnDefinition = "BIGINT")
	@Comment("파일이 저장되는 entity pk")
	private Long entityId;

	@Column(name = "entity_type", columnDefinition = "VARCHAR(20)")
	@Comment("파일이 저장되는 entity 이름")
	private String entityType;
}
