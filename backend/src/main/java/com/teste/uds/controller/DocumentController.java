package com.teste.uds.controller;

import com.teste.uds.domain.enums.DocumentStatus;
import com.teste.uds.dto.response.DocumentDetailResponse;
import com.teste.uds.dto.request.DocumentRequest;
import com.teste.uds.dto.response.DocumentResponse;
import com.teste.uds.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping
    public ResponseEntity<Page<DocumentResponse>> list(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) DocumentStatus status,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(documentService.listAll(title, status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDetailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getById(id));
    }

    @PostMapping("/{id}/upload")
    public ResponseEntity<Void> uploadFile(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails user) {
        documentService.uploadVersion(id, file, user.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam DocumentStatus status) {
        documentService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DocumentResponse> create(
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(documentService.createWithFile(title, file, user.getUsername()));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        try {
            String filePath = documentService.getFilePath(id);
            Path path = Paths.get(filePath);
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new RuntimeException("Erro ao ler o arquivo no servidor");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
