package com.teste.uds.service;

import com.teste.uds.domain.entity.Document;
import com.teste.uds.domain.entity.DocumentVersion;
import com.teste.uds.domain.enums.DocumentStatus;
import com.teste.uds.dto.response.DocumentDetailResponse;
import com.teste.uds.dto.request.DocumentRequest;
import com.teste.uds.dto.response.DocumentResponse;
import com.teste.uds.dto.response.VersionResponse;
import com.teste.uds.repository.DocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final StorageService storageService;

    @Transactional
    public DocumentResponse createWithFile(String title, MultipartFile file, String currentUser) {
        String fileKey = storageService.store(file);

        var document = Document.builder()
                .title(title)
                .status(DocumentStatus.DRAFT)
                .owner(currentUser)
                .build();

        var version = DocumentVersion.builder()
                .document(document)
                .fileKey(fileKey)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .uploadedBy(currentUser)
                .build();

        document.setVersions(new java.util.ArrayList<>(java.util.List.of(version)));
        return convertToResponse(documentRepository.save(document));
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponse> listAll(String title, DocumentStatus status, Pageable pageable) {
        return documentRepository.findByFilters(title, status, pageable)
                .map(this::convertToResponse);
    }
    @Transactional
    public DocumentResponse create(DocumentRequest request, String currentUser) {
        var document = Document.builder()
                .title(request.title())
                .description(request.description())
                .tags(request.tags())
                .status(request.status())
                .owner(currentUser)
                .build();

        return convertToResponse(documentRepository.save(document));
    }

    @Transactional
    public void uploadVersion(Long documentId, MultipartFile file, String currentUser) {
        var document = documentRepository.findById(documentId)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado"));

        String fileKey = storageService.store(file);

        var version = DocumentVersion.builder()
                .document(document)
                .fileKey(fileKey)
                .fileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .uploadedBy(currentUser)
                .build();
        if (document.getVersions() == null) {
            document.setVersions(new java.util.ArrayList<>());
        }

        document.getVersions().add(version);
        document.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(document);
    }

    private DocumentResponse convertToResponse(Document doc) {
        return new DocumentResponse(doc.getId(), doc.getTitle(), doc.getDescription(),
                doc.getTags(), doc.getOwner(), doc.getStatus(),
                doc.getCreatedAt(), doc.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public DocumentDetailResponse getById(Long id) {
        return documentRepository.findByIdWithVersions(id)
                .map(this::convertToDetailResponse)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado com ID: " + id));
    }

    @Transactional
    public void updateStatus(Long id, DocumentStatus status) {
        var document = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado com ID: " + id));

        document.setStatus(status);
        document.setUpdatedAt(LocalDateTime.now());
        documentRepository.save(document);
    }

    private DocumentDetailResponse convertToDetailResponse(Document doc) {
        List<VersionResponse> versions = doc.getVersions().stream()
                .map(v -> new VersionResponse(
                        v.getId(),
                        v.getFileName(),
                        v.getFileKey(),
                        v.getContentType(),
                        v.getUploadedAt(),
                        v.getUploadedBy()
                ))
                .toList();

        return new DocumentDetailResponse(
                doc.getId(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getTags(),
                doc.getOwner(),
                doc.getStatus(),
                doc.getCreatedAt(),
                doc.getUpdatedAt(),
                versions
        );
    }

    @Transactional(readOnly = true)
    public String getFilePath(Long id) {
        return documentRepository.findById(id)
                .map(doc -> {
                    if (doc.getVersions() != null && !doc.getVersions().isEmpty()) {
                        var ultimaVersao = doc.getVersions().get(doc.getVersions().size() - 1);
                        return ultimaVersao.getFileKey();
                    }
                    throw new RuntimeException("Documento não possui versões de arquivo.");
                })
                .orElseThrow(() -> new EntityNotFoundException("Documento não encontrado com ID: " + id));
    }


}

