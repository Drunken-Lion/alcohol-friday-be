package com.drunkenlion.alcoholfriday.global.file.application;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    NcpFileResponse saveFiles(BaseEntity entity, List<MultipartFile> multipartFiles);

    NcpFileResponse findAll(BaseEntity entity);

    NcpFileResponse findOne(BaseEntity entity);

    NcpFileResponse updateFiles(BaseEntity entity, List<Integer> removeSeq, List<MultipartFile> multipartFiles);

    void deleteFiles(BaseEntity entity);
    List<NcpFileResponse> findAllByEntityIds(List<Long> entityIds, String entityType);

    NcpFileResponse findByEntityId(Long entityId, String entityType);

    NcpFileResponse findByEntityId(Long entityId, EntityType entityType);

    NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, String EntityType);

    NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, EntityType entityType);
}
