package com.teste.uds.service;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileSystemStorageService implements StorageService {
    private final Path rootLocation = Paths.get("upload-dir");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar o diretório de upload", e);
        }
    }

    @Override
    public String store(MultipartFile file) {
        try {
            String fileKey = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.rootLocation.resolve(fileKey));
            return fileKey;
        } catch (IOException e) {
            throw new RuntimeException("Falha ao armazenar o arquivo", e);
        }
    }

    @Override
    public Resource load(String fileKey) {
        try {
            Path file = rootLocation.resolve(fileKey);
            return new UrlResource(file.toUri());
        } catch (MalformedURLException e) {
            throw new RuntimeException("Arquivo não encontrado", e);
        }
    }
}
