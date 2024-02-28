package com.drunkenlion.alcoholfriday.global.ncp.dto;

import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import com.drunkenlion.alcoholfriday.global.ncp.util.vo.FileInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@Schema(description = "파일 요청시 응답")
public class NcpFileResponse {
    @Schema(description = "file")
    private List<FileInfo> file;

    @Schema(description = "해당 entity의 식별자")
    private Long entityId;

    @Schema(description = "해당 entity의 타입")
    private String entityType;

    public static List<NcpFileResponse> of(List<NcpFile> ncpFiles) {
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
                            .seq((Integer) json.get("seq"))
                            .keyName((String) json.get("keyName"))
                            .path((String) json.get("path"))
                            .build()
                    ).toList();
        }

        return NcpFileResponse.builder()
                .file(files)
                .entityId(ncpFile.getEntityId())
                .entityType(ncpFile.getEntityType())
                .build();
    }
}
