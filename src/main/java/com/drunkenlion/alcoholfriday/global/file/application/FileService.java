package com.drunkenlion.alcoholfriday.global.file.application;

import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;

public interface FileService {
    List<NcpFileResponse> findAllByEntityIds(List<Long> entityIds, String entityType);

    NcpFileResponse findByEntityId(Long entityId, String entityType);
    NcpFileResponse findByEntityId(Long entityId, EntityType entityType);

    NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, String EntityType);
}
