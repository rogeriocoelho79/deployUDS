package com.teste.uds.dto.response;

import com.teste.uds.domain.enums.DocumentStatus;
import java.time.LocalDateTime;
import java.util.List;

public record DocumentResponse(
        Long id,
        String title,
        String description,
        List<String> tags,
        String owner,
        DocumentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
