package com.drunkenlion.alcoholfriday.global.ncp.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.drunkenlion.alcoholfriday.global.ncp.config.NcpS3Properties;
import com.drunkenlion.alcoholfriday.global.ncp.entity.NcpFile;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NcpS3ServiceImpl implements NcpS3Service {
	private final NcpS3Properties ncpS3Properties;
	private final AmazonS3Client amazonS3Client;

	/**
	 * Ncp Object Storage 파일 업로드
	 */
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
			String newFileName = String.format("%s+%d+%d_%s_%s",
				entityType, entityId, seq, UUID.randomUUID(), originalFileName);

			try (InputStream inputStream = multipartFile.getInputStream()) {
				String keyName = entityType + "/" + folderName + "/" + newFileName;

				amazonS3Client.putObject(
					new PutObjectRequest(
						ncpS3Properties.getS3().getBucketName(), keyName, inputStream, objectMetadata)
						.withCannedAcl(CannedAccessControlList.PublicRead));

				String uploadPath = amazonS3Client.getUrl(ncpS3Properties.getS3().getBucketName(), keyName).toString();

				Map<String, Object> fileMap = Map.of(
					"key_name", keyName,
					"path", uploadPath,
					"seq", seq);

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

	private String createFolderNameWithTodayDate() {
		LocalDateTime now = LocalDateTime.now();
		String year = String.valueOf(now.getYear());
		String month = String.format("%02d", now.getMonthValue());

		return year + "/" + month;
	}

	/**
	 * Ncp Object Storage 파일 삭제
	 */
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
}
