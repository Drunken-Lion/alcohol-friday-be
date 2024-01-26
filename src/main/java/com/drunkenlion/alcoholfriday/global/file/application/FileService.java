package com.drunkenlion.alcoholfriday.global.file.application;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;

public interface FileService {
	List<NcpFileResponse> findAllFiles();

	NcpFileResponse findEntityFiles(Long entityId, String entityType);

	NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, String EntityType);
}
