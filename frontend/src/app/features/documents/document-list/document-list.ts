import { Component, OnInit, inject, signal, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { DocumentService } from '../../../core/services/document.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-document-list',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './document-list.html',
  styleUrl: './document-list.scss',
  encapsulation: ViewEncapsulation.None
})
export class DocumentListComponent implements OnInit {
  private documentService = inject(DocumentService);
  private authService = inject(AuthService);

  // Estados reativos
  documents = signal<any[]>([]);
  totalElements = signal(0);
  currentPage = signal(0);
  showUploadModal = signal(false);
  
  // Filtros e Seleção
  filterTitle = '';
  filterStatus = '';
  selectedDocumentId: number | null = null;
  selectedFile: File | null = null;

  // Método obrigatório da interface OnInit
  ngOnInit(): void {
    this.loadDocuments();
  }

  loadDocuments(): void {
    this.documentService.list(this.currentPage(), 10, this.filterTitle, this.filterStatus)
      .subscribe({
        next: (res) => {
          this.documents.set(res.content);
          this.totalElements.set(res.totalElements);
        },
        error: (err) => console.error('Erro ao carregar documentos', err)
      });
  }

  applyFilter(): void {
    this.currentPage.set(0);
    this.loadDocuments();
  }

  changePage(delta: number): void {
    this.currentPage.update(p => p + delta);
    this.loadDocuments();
  }

  // Abre modal para novo documento
  openCreateModal(): void {
    this.selectedDocumentId = null;
    this.selectedFile = null;
    this.showUploadModal.set(true);
  }

  // Abre modal para nova versão de documento existente
  openUpload(id: number): void {
    this.selectedDocumentId = id;
    this.selectedFile = null;
    this.showUploadModal.set(true);
  }

  onFileSelected(event: any): void {
    const files = event.target.files;
    if (files && files.length > 0) {
      this.selectedFile = files[0];
    }
  }

  confirmUpload(): void {
    if (!this.selectedFile) return;

    if (this.selectedDocumentId) {
      // Atualizar versão
      this.documentService.upload(this.selectedDocumentId, this.selectedFile).subscribe({
        next: () => this.handleSuccess(),
        error: (err) => alert('Erro ao enviar nova versão')
      });
    } else {
      // Criar novo documento
      // Busca o valor do input de título via seletor simples para o teste
      const titleInput = document.querySelector('input[type="text"][placeholder*="Título"]') as HTMLInputElement;
      const title = titleInput?.value || 'Novo Documento';

      this.documentService.createWithFile(title, this.selectedFile).subscribe({
        next: () => this.handleSuccess(),
        error: (err) => alert('Erro ao criar documento')
      });
    }
  }

  handleSuccess(): void {
    alert('Operação realizada com sucesso!');
    this.closeModal();
    this.loadDocuments();
  }

  closeModal(): void {
    this.showUploadModal.set(false);
    this.selectedDocumentId = null;
    this.selectedFile = null;
  }

  logout(): void {
    this.authService.logout();
  }

  viewDetails(id: number): void {
  this.documentService.download(id).subscribe({
    next: (blob) => {
      const url = window.URL.createObjectURL(blob);
      window.open(url, '_blank');
    },
    error: (err) => alert('Erro ao baixar o arquivo: ' + err.message)
  });
}

}
