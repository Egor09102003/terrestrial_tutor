package com.example.terrestrial_tutor.service.impl;

import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.payload.response.FilesResponse;
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

    public Set<String> uploadFiles(Set<MultipartFile> files) throws IOException {
        Set<String> filesList = new HashSet<>();
        for (MultipartFile file : files) {
            if (file != null) {
                File uploadDir = new File(uploadPath);

                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String fileName = file.getOriginalFilename();

                try {
                    File newFile = new File(uploadDir + "/" + fileName);
                    if (!newFile.exists()) {
                        file.transferTo(newFile.toPath());
                    }
                    filesList.add(fileName);
                } catch (IOException e) {
                    throw new IOException("Error while saving file '" + fileName + "': " + e.getMessage());
                }
            }
        }

        return filesList;
    }

    public Set<FilesResponse> getFilesByPaths(Set<String> filesList) throws IOException {
        Set<FilesResponse> files = new HashSet<>();
        try {
            for (String savedFile : filesList) {
                File file = new File(uploadPath + savedFile);
                if (file.exists()) {
                    MultipartFile curFile = new MockMultipartFile(savedFile, "", null, Files.readAllBytes(Paths.get(uploadPath + savedFile)));
                    files.add(new FilesResponse(savedFile, curFile.getBytes()));
                }
            }
        } catch (IOException e) {
            throw new IOException("Error while get file: " + e.getMessage());
        }
        return files;
    }
}
