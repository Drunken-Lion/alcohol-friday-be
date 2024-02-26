package com.drunkenlion.alcoholfriday.global.ncp.application;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface NcpS3Service {
    NcpFile saveFiles(BaseEntity entity, List<MultipartFile> files);

    NcpFile ncpUploadFiles(List<MultipartFile> multipartFiles, Long entityId, String entityType);

    void ncpDeleteFile(String keyName);
}
