package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.service.UploadFilesService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UploadFilesImpl implements UploadFilesService {

    private static final Logger log = LoggerFactory.getLogger(UploadFilesImpl.class);
    @Value("${upload.path}")
    private String uploadPath;

    public Set<String> uploadFiles(Set<MultipartFile> files) {
        Set<String> filesList = new HashSet<>();
        for (MultipartFile file : files) {
            if (file != null) {
                File uploadDir = new File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String fileName = file.getOriginalFilename();
                String fileUID = UUID.randomUUID().toString();

                try {
                    File newFile = new File(uploadDir + "/" + fileUID + "$" + fileName);
                    file.transferTo(newFile.toPath());
                    filesList.add(fileUID + "$" + fileName);
                } catch (IOException e) {
                    log.error("Error while saving file '{}': {}", fileName, e.getMessage());
                    return new HashSet<>();
                }
            }
        }

        return filesList;
    }

    public byte[] getFilesByPaths(String fileName) {
        byte[] resultFile = new byte[0];
        try {
            File file = new File(uploadPath + fileName);
            if (file.exists()) {
                MultipartFile curFile = new MockMultipartFile(
                        fileName,
                        "",
                        null,
                        Files.readAllBytes(Paths.get(uploadPath + fileName))
                );
                resultFile = curFile.getBytes();
            }
        } catch (IOException e) {
            log.error("Error while get file: {}", e.getMessage());
        }
        return resultFile;
    }
}
