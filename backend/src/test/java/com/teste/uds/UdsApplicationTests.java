package com.teste.uds;

import com.teste.uds.service.DocumentService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.teste.uds.domain.entity.Document;
import com.teste.uds.domain.enums.DocumentStatus;
import com.teste.uds.dto.request.DocumentRequest;
import com.teste.uds.repository.DocumentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

	@Mock
	private DocumentRepository documentRepository;

	@InjectMocks
	private DocumentService documentService;

	@Test
	@DisplayName("Deve criar um documento com sucesso")
	void shouldCreateDocumentSuccess() {

		var request = new DocumentRequest("Título Teste", "Desc", List.of("tag1"), DocumentStatus.DRAFT);
		var document = Document.builder().id(1L).title("Título Teste").build();

		when(documentRepository.save(any(Document.class))).thenReturn(document);

		var response = documentService.create(request, "admin_user");

		assertNotNull(response);
		assertEquals("Título Teste", response.title());
		verify(documentRepository, times(1)).save(any(Document.class));
	}

	@Test
	@DisplayName("Deve lançar exceção ao tentar buscar documento inexistente")
	void shouldThrowExceptionWhenDocumentNotFound() {

		when(documentRepository.findByIdWithVersions(anyLong())).thenReturn(Optional.empty());

		assertThrows(EntityNotFoundException.class, () -> {
			documentService.getById(1L);
		});
		verify(documentRepository, times(1)).findByIdWithVersions(1L);
	}

	@Test
	@DisplayName("Deve atualizar o status do documento com sucesso")
	void shouldUpdateDocumentStatus() {

		var document = Document.builder().id(1L).status(DocumentStatus.DRAFT).build();
		when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
		documentService.updateStatus(1L, DocumentStatus.PUBLISHED);

		assertEquals(DocumentStatus.PUBLISHED, document.getStatus());
		verify(documentRepository, times(1)).save(document);
	}
}
