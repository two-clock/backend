package com.twoclock.gitconnect.global.util;

import com.twoclock.gitconnect.global.model.FileDto;
import lombok.NonNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public class FileUtil {

    public static FileDto convertFileToFileUploadDto(@NonNull MultipartFile multipartFile) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String fileName = multipartFile.getOriginalFilename();
        String fileExtension = multipartFile.getOriginalFilename().substring(fileName.lastIndexOf(".") + 1);
//        String contentType = multipartFile.getContentType();
        int fileSize = (int) multipartFile.getSize();
        return new FileDto(uuid, fileName, fileExtension, fileSize);
    }
}
