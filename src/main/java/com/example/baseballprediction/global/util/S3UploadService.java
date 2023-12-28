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
		
		return amazonS3.getUrl(bucket, saveFileName).toString();
	}

	private String generateFileName(String originFileName) {
		return UUID.randomUUID() + originFileName;
	}
}
