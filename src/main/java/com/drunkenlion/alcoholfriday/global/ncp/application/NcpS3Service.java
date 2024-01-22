package com.drunkenlion.alcoholfriday.global.ncp.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.drunkenlion.alcoholfriday.global.ncp.config.NcpS3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NcpS3Service {
    private final NcpS3Properties ncpS3Properties;
    private final AmazonS3Client amazonS3Client;

    public void uploadImages(List<MultipartFile> multipartFiles, String filePath, Long id) {

        for (MultipartFile multipartFile : multipartFiles) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getSize());
            objectMetadata.setContentType(multipartFile.getContentType());

            String originalFileName = multipartFile.getOriginalFilename();
            String newFileName = filePath + "_" + id + "_" + UUID.randomUUID() + "_" + originalFileName;
            String folderName = createFolderNameWithTodayDate();
            String keyName = filePath + "/" + folderName + "/" + newFileName;
            String uploadUrl = "";

            try (InputStream inputStream = multipartFile.getInputStream()) {

                amazonS3Client.putObject(
                        new PutObjectRequest(
                                ncpS3Properties.getS3().getBucketName(), keyName, inputStream, objectMetadata)
                                .withCannedAcl(CannedAccessControlList.PublicRead));

                uploadUrl = ncpS3Properties.getS3().getEndPoint() + "/" + ncpS3Properties.getS3().getBucketName() + "/" + keyName;

            } catch (SdkClientException e) {
                throw new SdkClientException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String createFolderNameWithTodayDate() {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());

        return year + "/" + month;
    }

    public void deleteImage(String keyName) {
        try {
            amazonS3Client.deleteObject(ncpS3Properties.getS3().getBucketName(), keyName);
        } catch (AmazonS3Exception e) {
            throw new AmazonS3Exception(e.getMessage());
        } catch (SdkClientException e) {
            throw new SdkClientException(e.getMessage());
        }
    }
}
