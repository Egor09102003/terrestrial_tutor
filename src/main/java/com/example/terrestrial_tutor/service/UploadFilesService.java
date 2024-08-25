package com.example.terrestrial_tutor.service;

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
    */
    Set<String> uploadFiles(Set<MultipartFile> file) throws IOException;
    byte[] getFilesByPaths(String path) throws IOException;
}
