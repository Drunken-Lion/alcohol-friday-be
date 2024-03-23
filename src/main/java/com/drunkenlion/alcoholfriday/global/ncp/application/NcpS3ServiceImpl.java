package com.drunkenlion.alcoholfriday.global.ncp.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.dao.NoticeRepository;
import com.drunkenlion.alcoholfriday.domain.customerservice.notice.entity.Notice;
import com.drunkenlion.alcoholfriday.global.common.entity.BaseEntity;
import com.drunkenlion.alcoholfriday.global.common.enumerated.EntityTypeV2;
import com.drunkenlion.alcoholfriday.global.common.response.HttpResponse;
import com.drunkenlion.alcoholfriday.global.exception.BusinessException;
import com.drunkenlion.alcoholfriday.global.ncp.config.NcpS3Properties;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class NcpS3ServiceImpl implements NcpS3Service {
    private final NoticeRepository noticeRepository;
    private final NcpS3Properties ncpS3Properties;
    private final AmazonS3Client amazonS3Client;

    /**
     * NCP S3 bucket save
     */
    @Override
    public NcpFile saveFiles(BaseEntity entity, List<MultipartFile> files) {
        List<Map<String, Object>> fileMaps = new ArrayList<>();

        // thread safe AtomicInteger 사용
        AtomicInteger seq = new AtomicInteger(1);
        for (MultipartFile file : files) {
            String keyName = generateFileName(entity, file);
            uploadBucket(file, keyName);
            String path = generatePath(keyName);
            fileMaps.add(createMap(seq.getAndIncrement(), keyName, path));
        }

        return NcpFile.builder()
                .entityId(entity.getId())
                .entityType(EntityTypeV2.getEntityType(entity))
                .s3Files(fileMaps)
                .build();
    }

    @Override
    public String saveFile(Long id, MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException(HttpResponse.Fail.NOT_FOUND_NOTICE);
        }

        Notice entity = noticeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpResponse.Fail.NOT_FOUND_NOTICE));

        String keyName = generateFileName(entity, file);
        uploadBucket(file, keyName);

        return generatePath(keyName);
    }

    @Override
    public Map<String, Object> updateFile(BaseEntity entity, int seq, MultipartFile file) {
        String keyName = generateFileName(entity, file);
        uploadBucket(file, keyName);
        String path = generatePath(keyName);
        return createMap(seq, keyName, path);
    }

    /**
     * 2024.03.01 삭제 예정
     */
    @Deprecated
    @Override
    public NcpFile ncpUploadFiles(List<MultipartFile> multipartFiles, Long entityId, String entityType) {
        List<Map<String, Object>> fileMaps = new ArrayList<>();

        int seq = 1;

        for (MultipartFile multipartFile : multipartFiles) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            String folderName = createFolderNameWithTodayDate();
            String originalFileName = multipartFile.getOriginalFilename();
            String newFileName = String.format("%s+%d_%s_%s",
                    entityType, entityId, UUID.randomUUID(), originalFileName);

            try (InputStream inputStream = multipartFile.getInputStream()) {
                String keyName = entityType + "/" + folderName + "/" + newFileName;

                amazonS3Client.putObject(
                        new PutObjectRequest(
                                ncpS3Properties.getS3().getBucketName(), keyName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));

                String uploadPath = generatePath(keyName);

                Map<String, Object> fileMap = Map.of(
                        "seq", seq,
                        "keyName", keyName,
                        "path", uploadPath);

                fileMaps.add(fileMap);

                seq++;

            } catch (SdkClientException e) {
                throw new SdkClientException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return NcpFile.builder()
                .entityId(entityId)
                .entityType(entityType)
                .s3Files(fileMaps)
                .build();
    }

    /**
     * 2024.03.01 삭제 예정
     */
    @Deprecated
    @Override
    public void ncpDeleteFile(String keyName) {
        try {
            amazonS3Client.deleteObject(ncpS3Properties.getS3().getBucketName(), keyName);
        } catch (AmazonS3Exception e) {
            throw new AmazonS3Exception(e.getMessage());
        } catch (SdkClientException e) {
            throw new SdkClientException(e.getMessage());
        }
    }

    private String generateFileName(BaseEntity entity, MultipartFile file) {
        String entityType = EntityTypeV2.getEntityType(entity);
        String folderName = createFolderNameWithTodayDate();
        String originalFileName = file.getOriginalFilename();
        String newFileName =
                "%s+%d_%s_%s".formatted(entityType, entity.getId(), UUID.randomUUID(), originalFileName);
        return entityType + "/" + folderName + "/" + newFileName;
    }
    private void uploadBucket(MultipartFile file, String keyName) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        try (InputStream inputStream = file.getInputStream()) {
            amazonS3Client.putObject(
                    new PutObjectRequest(
                            ncpS3Properties.getS3().getBucketName(), keyName, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (SdkClientException e) {
            log.info("[NcpS3ServiceImpl.saveFilesToNCP] SdkClientException");
            throw new SdkClientException(e);
        } catch (IOException e) {
            log.info("[NcpS3ServiceImpl.saveFilesToNCP] IOException");
            throw new RuntimeException(e);
        }
    }
    private String generatePath(String keyName) {
        return amazonS3Client.getUrl(ncpS3Properties.getS3().getBucketName(), keyName).toString();
    }
    private String createFolderNameWithTodayDate() {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        return year + "/" + month;
    }
    private Map<String, Object> createMap(int seq, String keyName, String path) {
        Map<String, Object> fileMap = new LinkedHashMap<>();
        fileMap.put("seq", seq);
        fileMap.put("keyName", keyName);
        fileMap.put("path", path);
        return fileMap;
    }
}
