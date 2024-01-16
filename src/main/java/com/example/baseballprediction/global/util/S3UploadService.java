package com.example.baseballprediction.global.util;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.baseballprediction.global.constant.ImageType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class S3UploadService {
	private final AmazonS3 amazonS3;

	@Value("${cloud.aws.s3.bucket}")
	private String bucket;

	@Value("${cloud.aws.s3.domain}")
	private String domain;

	public String updateFile(MultipartFile multipartFile, String originFileName, ImageType imageType) {
		deleteFile(originFileName);

		String updateFileName = saveFile(multipartFile, imageType);

		return updateFileName;
	}

	public String saveFile(MultipartFile multipartFile, ImageType imageType) {
		String originFileName = multipartFile.getOriginalFilename();
		String saveFileName = imageType.getFolderName() + "/" + generateFileName(originFileName);

		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentLength(multipartFile.getSize());
		metadata.setContentType(multipartFile.getContentType());

		try {
			amazonS3.putObject(bucket, saveFileName, multipartFile.getInputStream(),
				metadata);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return amazonS3.getUrl(bucket, saveFileName).toString().replace(domain, "");
	}

	private String generateFileName(String originFileName) {
		return UUID.randomUUID() + originFileName.substring(originFileName.indexOf("."));
	}

	private void deleteFile(String fileName) {
		if (fileName == null)
			return;

		amazonS3.deleteObject(bucket, fileName);
	}
}
