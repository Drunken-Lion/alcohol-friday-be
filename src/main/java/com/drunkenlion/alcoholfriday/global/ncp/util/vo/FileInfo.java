package com.drunkenlion.alcoholfriday.global.ncp.util.vo;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class FileInfo {
    private String keyName;
    private String path;
    private Integer seq;
}
