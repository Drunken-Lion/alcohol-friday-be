package com.drunkenlion.alcoholfriday.global.file.application;

import com.amazonaws.services.kms.model.NotFoundException;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.file.dao.FileRepository;
import com.drunkenlion.alcoholfriday.global.ncp.application.NcpS3Service;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FileServiceImpl implements FileService {
    private final NcpS3Service ncpS3Service;
    private final FileRepository fileRepository;

    /**
     * new 파일 저장 (Ncp & DB)
     * Entity 데이터 save 후 id 값이 발급된 이후 저장 필요
     * 반환 객체에 대해 null 체크 필요
     */
    @Override
    public NcpFileResponse saveFiles(BaseEntity entity, List<MultipartFile> multipartFiles) {
        if (entity.getId() == null || entity.getId() == 0) {
            throw new NotFoundException("Entity does not have an id");
        }

        if (multipartFiles.get(0).isEmpty()) { // MultipartFile 빈 값일 경우 배열의 0번째 값이 empty이다.
            return null;
        }

        NcpFile ncpFile = ncpS3Service.saveFiles(entity, multipartFiles);
        return NcpFileResponse.of(fileRepository.save(ncpFile));
    }

    /**
     * old 여러개의 게시물에 있는 모든 이미지 조회
     */
    @Override
    public List<NcpFileResponse> findAllByEntityIds(List<Long> entityIds, String entityType) {
        List<NcpFile> ncpFiles =
                fileRepository.findAllByEntityIdInAndEntityType(entityIds, entityType);
        return NcpFileResponse.of(ncpFiles);
    }

    /**
     * old 하나의 게시물에 있는 모든 이미지 조회
     */
    @Override
    public NcpFileResponse findByEntityId(Long entityId, EntityType entityType) {
        NcpFile ncpFile =
                fileRepository.findByEntityIdAndEntityType(entityId, entityType.getEntityName()).orElse(null);
        return NcpFileResponse.of(ncpFile);
    }

    /**
     * old 파일 저장 (Ncp & DB)
     */
    @Transactional
    @Override
    public NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, EntityType entityType) {
        NcpFile ncpFile = ncpS3Service.ncpUploadFiles(multipartFiles, entityId, entityType.getEntityName());
        return NcpFileResponse.of(fileRepository.save(ncpFile));
    }

    /**
     * EntityType 매개변수로 받는 메서드를 이용해주세요. 2차 개발일정 시작 시 해당 메서드는 삭제 예정입니다. 하나의 게시물에 있는 모든 이미지 조회
     */
    @Deprecated
    @Override
    public NcpFileResponse findByEntityId(Long entityId, String entityType) {
        NcpFile ncpFile = this.fileRepository.findByEntityIdAndEntityType(entityId, entityType).orElse(null);

        return NcpFileResponse.of(ncpFile);
    }

    /**
     * EntityType 매개변수로 받는 메서드를 이용해주세요. 2차 개발일정 시작 시 해당 메서드는 삭제 예정입니다. 파일 저장 (Ncp & DB)
     */
    @Deprecated
    @Transactional
    @Override
    public NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, String entityType) {
        NcpFile ncpFile = ncpS3Service.ncpUploadFiles(multipartFiles, entityId, entityType);
        return NcpFileResponse.of(fileRepository.save(ncpFile));
    }
}
