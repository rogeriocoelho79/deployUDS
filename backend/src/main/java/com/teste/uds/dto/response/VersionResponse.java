package com.teste.uds.dto.response;

import java.time.LocalDateTime;

public record VersionResponse(
        Long id,
        String fileName,
        String fileKey,
        String contentType,
        LocalDateTime uploadedAt,
        String uploadedBy
) {}
