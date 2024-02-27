package com.drunkenlion.alcoholfriday.global.ncp.util.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
@Schema(description = "file 객체")
public class FileInfo {
    @Schema(description = "file의 순번")
    private Integer seq;

    @Schema(description = "file의 keyName")
    private String keyName;

    @Schema(description = "file의 full path")
    private String path;
}
