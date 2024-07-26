package com.example.terrestrial_tutor.service;

import com.example.terrestrial_tutor.entity.TaskEntity;
import com.example.terrestrial_tutor.payload.response.FilesResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

/**
 * Сервис для загрузки файлов
 */
public interface UploadFilesService {
    /**
    * Загрузка файлов
    *
    * @param file лист файлов
    * @return статус операции
    * @throws IOException
    */
    Set<String> uploadFiles(Set<MultipartFile> file, TaskEntity curTak) throws IOException;
    Set<FilesResponse> getFilesByPaths(Set<String> paths) throws IOException;
}
