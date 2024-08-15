package com.twoclock.gitconnect.global.s3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RequiredArgsConstructor
@Service
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucket;

    public String uploadFile(String keyName, MultipartFile file) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        PutObjectRequest request = new PutObjectRequest(bucket, keyName, file.getInputStream(), objectMetadata)
                .withCannedAcl(CannedAccessControlList.PublicRead);

        s3Client.putObject(request);

        return s3Client.getUrl(bucket, keyName).toString();
    }

    public void deleteFile(String keyName) {
        DeleteObjectRequest request = new DeleteObjectRequest(bucket, keyName);
        s3Client.deleteObject(request);
    }
}
