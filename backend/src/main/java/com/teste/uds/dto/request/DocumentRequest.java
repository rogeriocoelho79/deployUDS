package com.teste.uds.dto.request;

import com.teste.uds.domain.enums.DocumentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DocumentRequest(
        @NotBlank(message = "O título é obrigatório")
        String title,

        String description,

        List<String> tags,

        @NotNull(message = "O status é obrigatório")
        DocumentStatus status
) {}
