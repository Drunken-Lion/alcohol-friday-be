package com.drunkenlion.alcoholfriday.global.file.application;

import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.dao.FileRepository;
import com.drunkenlion.alcoholfriday.global.ncp.application.NcpS3Service;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {
    private final NcpS3Service ncpS3Service;
    private final FileRepository fileRepository;

    /**
     * 여러개의 게시물에 있는 모든 이미지 조회
     */
    @Override
    public List<NcpFileResponse> findAllByEntityIds(List<Long> entityIds, String entityType) {
        List<NcpFile> ncpFiles = this.fileRepository.findAllByEntityIdInAndEntityType(entityIds, entityType);

        return NcpFileResponse.of(ncpFiles);
    }

    /**
     * EntityType 매개변수로 받는 메서드를 이용해주세요.
     * 2차 개발일정 시작 시 해당 메서드는 삭제 예정입니다.
     * 하나의 게시물에 있는 모든 이미지 조회
     */
    @Deprecated
    @Override
    public NcpFileResponse findByEntityId(Long entityId, String entityType) {
        NcpFile ncpFile = this.fileRepository.findByEntityIdAndEntityType(entityId, entityType).orElse(null);

        return NcpFileResponse.of(ncpFile);
    }

    /**
     * 하나의 게시물에 있는 모든 이미지 조회
     */
    @Override
    public NcpFileResponse findByEntityId(Long entityId, EntityType entityType) {
        NcpFile ncpFile = this.fileRepository.findByEntityIdAndEntityType(entityId, entityType.getEntityName())
                .orElse(null);
        return NcpFileResponse.of(ncpFile);
    }

    /**
     * EntityType 매개변수로 받는 메서드를 이용해주세요.
     * 2차 개발일정 시작 시 해당 메서드는 삭제 예정입니다.
     * 파일 저장 (Ncp & DB)
     */
    @Deprecated
    @Transactional
    @Override
    public NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, String entityType) {
        NcpFile ncpFile = ncpS3Service.ncpUploadFiles(multipartFiles, entityId, entityType);
        return NcpFileResponse.of(fileRepository.save(ncpFile));
    }

    /**
     * 파일 저장 (Ncp & DB)
     */
    @Transactional
    @Override
    public NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, EntityType entityType) {
        NcpFile ncpFile = ncpS3Service.ncpUploadFiles(multipartFiles, entityId, entityType.getEntityName());
        return NcpFileResponse.of(fileRepository.save(ncpFile));
    }
}
