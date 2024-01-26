package com.drunkenlion.alcoholfriday.global.file.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.drunkenlion.alcoholfriday.global.file.dao.FileRepository;
import com.drunkenlion.alcoholfriday.global.ncp.application.NcpS3Service;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {
	private final NcpS3Service ncpS3Service;
	private final FileRepository fileRepository;

	/**
	 * NcpFile 테이블 모두 조회
	 */
	@Override
	public List<NcpFileResponse> findAllFiles() {
		List<NcpFile> ncpFileList = fileRepository.findAll();
		return ncpFileList.stream().map(NcpFileResponse::of).toList();
	}

	/**
	 * 하나의 게시물에 있는 모든 이미지 조회
	 */
	@Override
	public NcpFileResponse findEntityFiles(Long entityId, String entityType) {
		NcpFile ncpFile = fileRepository.findByEntityIdAndEntityType(entityId, entityType);
		return NcpFileResponse.of(ncpFile);
	}

	/**
	 * 파일 저장 (Ncp & DB)
	 */
	@Transactional
	@Override
	public NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, String entityType) {
		NcpFile ncpFile = ncpS3Service.ncpUploadFiles(multipartFiles, entityId, entityType);
		return NcpFileResponse.of(fileRepository.save(ncpFile));
	}
}
