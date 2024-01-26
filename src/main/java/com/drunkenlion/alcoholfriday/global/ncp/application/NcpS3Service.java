package com.drunkenlion.alcoholfriday.global.ncp.application;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;

public interface NcpS3Service {
	NcpFile ncpUploadFiles(List<MultipartFile> multipartFiles, Long entityId, String entityType);

	void ncpDeleteFile(String keyName);
}
