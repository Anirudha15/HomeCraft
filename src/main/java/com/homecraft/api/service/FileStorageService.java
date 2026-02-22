package com.homecraft.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {

    public String store(MultipartFile file, String baseDir) throws Exception {

        File dir = new File(baseDir);
        if (!dir.exists()) dir.mkdirs();

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path path = Path.of(baseDir, fileName);

        Files.copy(file.getInputStream(), path);

        return fileName;
    }
}