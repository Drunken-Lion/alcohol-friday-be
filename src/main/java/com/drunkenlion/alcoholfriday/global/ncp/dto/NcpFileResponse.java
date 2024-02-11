package com.drunkenlion.alcoholfriday.global.ncp.dto;

import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import com.drunkenlion.alcoholfriday.global.ncp.util.vo.FileInfo;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class NcpFileResponse {
	private List<FileInfo> file;
	private Long entityId;
	private String entityType;

	public static List<NcpFileResponse> of (List<NcpFile> ncpFiles) {
		return ncpFiles.stream()
				.map((NcpFileResponse::of))
				.toList();
	}

	public static NcpFileResponse of(NcpFile ncpFile) {
		// NcpFile의 s3Files 필드를 FileInfo 객체로 변환
		List<FileInfo> files = null;

		if (ncpFile != null && ncpFile.getS3Files() != null) {
			files = ncpFile.getS3Files().stream()
					.map(json -> FileInfo.builder()
							.keyName((String) json.get("key_name"))
							.path((String) json.get("path"))
							.seq((Integer) json.get("seq"))
							.build()
					).toList();
		}

		return NcpFileResponse.builder()
				.file(files)
				.build();
	}
}
