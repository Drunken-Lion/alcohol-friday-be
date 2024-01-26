package com.drunkenlion.alcoholfriday.global.ncp.dto;

import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class NcpFileResponse {
	private Long id;
	private List<FileInfo> s3Files;
	private Long entityId;
	private String entityType;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static NcpFileResponse of(NcpFile ncpFile) {
		// NcpFile의 s3Files 필드를 FileInfo 객체로 변환
		List<FileInfo> files = ncpFile.getS3Files().stream()
			.map(json -> new FileInfo(
				(String)json.get("key_name"),
				(String)json.get("path"),
				Long.valueOf((Integer)json.get("seq"))
			))
			.toList();

		return NcpFileResponse.builder()
			.id(ncpFile.getId())
			.s3Files(files)
			.entityId(ncpFile.getEntityId())
			.entityType(ncpFile.getEntityType())
			.createdAt(ncpFile.getCreatedAt())
			.updatedAt(ncpFile.getUpdatedAt())
			.build();
	}

	@Getter
	@AllArgsConstructor
	public static class FileInfo {
		private String keyName;
		private String path;
		private Long seq;
	}
}
