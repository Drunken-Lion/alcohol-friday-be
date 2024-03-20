package com.drunkenlion.alcoholfriday.global.file.application;

import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityType;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityTypeV2;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse.Fail;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.file.dao.FileRepository;
import com.drunkenlion.alcoholfriday.global.ncp.application.NcpS3Service;
import com.drunkenlion.alcoholfriday.global.ncp.dto.NcpFileResponse;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
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
     * 파일 저장 (Ncp & DB)
     */
    @Transactional
    @Override
    public NcpFileResponse saveFiles(BaseEntity entity, List<MultipartFile> multipartFiles) {
        if (entity.getId() == null || entity.getId() == 0) {
            throw new BusinessException(HttpResponse.Fail.NOT_FOUND);
        }

        if (multipartFiles.get(0).isEmpty()) { // MultipartFile 빈 값일 경우 배열의 0번째 값이 empty이다.
            return null;
        }

        NcpFile ncpFile = ncpS3Service.saveFiles(entity, multipartFiles);
        return NcpFileResponse.of(fileRepository.save(ncpFile));
    }

    /**
     * entity 이미지 전체 조회
     */
    @Override
    public NcpFileResponse findAll(BaseEntity entity) {
        Optional<NcpFile> opFile =
                fileRepository.findByEntityIdAndEntityType(entity.getId(), EntityTypeV2.getEntityType(entity));

        return opFile.map(NcpFileResponse::of).orElse(null);
    }

    /**
     * entity 이미지의 'seq : 1' 조회
     */
    @Override
    public NcpFileResponse findOne(BaseEntity entity) {
        NcpFile ncpFile1 =
                fileRepository.findByEntityIdAndEntityType(entity.getId(), EntityTypeV2.getEntityType(entity))
                        .orElse(null);

        if (ncpFile1 == null || ncpFile1.getS3Files().isEmpty()) {
            return null;
        }

        ncpFile1.updateFiles(List.of(ncpFile1.getS3Files().get(0)));
        return NcpFileResponse.of(ncpFile1);
    }

    /**
     * entity 이미지에서 List로 전달받은 번호를 통해 동일한 seq 삭제
     */
    @Transactional
    @Override
    public NcpFileResponse updateFiles(BaseEntity entity, List<Integer> removeSeq, List<MultipartFile> multipartFiles) {
        Optional<NcpFile> opNcpFile = fileRepository.findByEntityIdAndEntityType(entity.getId(),
                EntityTypeV2.getEntityType(entity));

        if (opNcpFile.isEmpty()) {
            return saveFiles(entity, multipartFiles);
        }

        NcpFile ncpFile = opNcpFile.get();

        // removeSeq 내 seq 값과 일치하는 이미지 삭제
        if (removeSeq != null) {
            if (!removeSeq.isEmpty()) {
                removeSeq.forEach(seq -> ncpFile.getS3Files().stream()
                        .filter(files -> Objects.equals(seq, files.get("seq")))
                        .findFirst()
                        .ifPresent(file -> {
                            ncpS3Service.ncpDeleteFile((String) file.get("keyName"));
                            ncpFile.getS3Files().remove(file);
                        }));

                // seq 재정렬
                AtomicInteger seq = new AtomicInteger(1);
                ncpFile.getS3Files().forEach(file -> file.put("seq", seq.getAndIncrement()));
            }
        }

        // 추가 이미지 저장
        if (multipartFiles != null) {
            if (!multipartFiles.get(0).isEmpty()) {
                AtomicInteger seq = new AtomicInteger(ncpFile.getS3Files().size() + 1);
                multipartFiles.forEach(
                        file -> ncpFile.getS3Files().add(ncpS3Service.updateFile(entity, seq.getAndIncrement(), file)));
            }
        }

        fileRepository.save(ncpFile);
        return NcpFileResponse.of(ncpFile);
    }

    @Override
    public void deleteFiles(BaseEntity entity) {
        fileRepository.findByEntityIdAndEntityType(entity.getId(), EntityTypeV2.getEntityType(entity))
                .ifPresent(fileRepository::delete);
    }

    /**
     * 2024.03.01 삭제) 여러개의 게시물에 있는 모든 이미지 조회
     */
    @Deprecated
    @Override
    public List<NcpFileResponse> findAllByEntityIds(List<Long> entityIds, String entityType) {
        List<NcpFile> ncpFiles =
                fileRepository.findAllByEntityIdInAndEntityType(entityIds, entityType);
        return NcpFileResponse.of(ncpFiles);
    }

    /**
     * 2024.03.01 삭제) old 하나의 게시물에 있는 모든 이미지 조회
     */
    @Deprecated
    @Override
    public NcpFileResponse findByEntityId(Long entityId, EntityType entityType) {
        NcpFile ncpFile =
                fileRepository.findByEntityIdAndEntityType(entityId, entityType.getEntityName()).orElse(null);
        return NcpFileResponse.of(ncpFile);
    }

    /**
     * 2024.03.01 삭제) old 파일 저장 (Ncp & DB)
     */
    @Deprecated
    @Transactional
    @Override
    public NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, EntityType entityType) {
        NcpFile ncpFile = ncpS3Service.ncpUploadFiles(multipartFiles, entityId, entityType.getEntityName());
        return NcpFileResponse.of(fileRepository.save(ncpFile));
    }

    /**
     * 2024.03.01 삭제) EntityType 매개변수로 받는 메서드를 이용해주세요. 2차 개발일정 시작 시 해당 메서드는 삭제 예정입니다. 하나의 게시물에 있는 모든 이미지 조회
     */
    @Deprecated
    @Override
    public NcpFileResponse findByEntityId(Long entityId, String entityType) {
        NcpFile ncpFile = this.fileRepository.findByEntityIdAndEntityType(entityId, entityType).orElse(null);

        return NcpFileResponse.of(ncpFile);
    }

    /**
     * 2024.03.01 삭제) EntityType 매개변수로 받는 메서드를 이용해주세요. 2차 개발일정 시작 시 해당 메서드는 삭제 예정입니다. 파일 저장 (Ncp & DB)
     */
    @Deprecated
    @Transactional
    @Override
    public NcpFileResponse uploadFiles(List<MultipartFile> multipartFiles, Long entityId, String entityType) {
        NcpFile ncpFile = ncpS3Service.ncpUploadFiles(multipartFiles, entityId, entityType);
        return NcpFileResponse.of(fileRepository.save(ncpFile));
    }
}
