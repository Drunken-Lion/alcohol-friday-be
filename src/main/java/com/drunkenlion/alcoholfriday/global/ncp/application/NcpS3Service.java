package com.drunkenlion.alcoholfriday.global.ncp.application;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface NcpS3Service {
    NcpFile saveFiles(BaseEntity entity, List<MultipartFile> files);

    String saveFile(Long id, MultipartFile files);

    Map<String, Object> updateFile(BaseEntity entity, int seq, MultipartFile file);

    NcpFile ncpUploadFiles(List<MultipartFile> multipartFiles, Long entityId, String entityType);

    void ncpDeleteFile(String keyName);
}
