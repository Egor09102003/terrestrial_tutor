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

    public Set<String> uploadFiles(Set<MultipartFile> files, TaskEntity curTask) throws IOException {
        Set<String> filesList = new HashSet<>();
        for (MultipartFile file : files) {
            if (file != null) {
                File uploadDir;
                if (file.getOriginalFilename().endsWith(".jpg") || file.getOriginalFilename().endsWith(".png")) {
                    uploadDir = new File(uploadPath + "/images");
                } else if (file.getOriginalFilename().endsWith(".pdf")) {
                    uploadDir = new File(uploadPath + "/pdfs");
                } else {
                    uploadDir = new File(uploadPath + "/other_files");
                }

                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                String fileUuid = UUID.randomUUID().toString();
                String fileName = uploadDir + "/" + fileUuid + "." + file.getOriginalFilename();

                boolean fileExists = false;
                if (curTask != null) {
                    for (String savedFile : curTask.getFiles()) {
                        if (savedFile.equals(file.getOriginalFilename())) {
                            fileExists = true;
                            filesList.add(savedFile);
                            break;
                        }
                    }
                }

                if (!fileExists) {
                    file.transferTo(new File(fileName).toPath());
                    filesList.add(fileName.substring(fileName.lastIndexOf("assets")));
                }
            }
        }

        if (curTask != null) {
            for (String savedFile : curTask.getFiles()) {
                if (!filesList.contains(savedFile)) {
                    File notExistedFile = new File("/" + savedFile);
                    if (notExistedFile.delete()) {
                        log.info("File {} has been removed", savedFile);
                    }
                }
            }
        }

        return filesList;
    }

    public Set<FilesResponse> getFilesByPaths(Set<String> paths) throws IOException {
        Set<FilesResponse> files = new HashSet<>();
        for (String path : paths) {
            File file = new File("/" + path);
            if (file.exists()) {
                MultipartFile curFile = new MockMultipartFile(path, "", null, Files.readAllBytes(Paths.get("/" + path)));
                files.add(new FilesResponse(curFile.getName(), curFile.getBytes()));
            }
        }
        return files;
    }
}
