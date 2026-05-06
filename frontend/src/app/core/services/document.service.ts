import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class DocumentService {
  private http = inject(HttpClient);
  private readonly API = 'http://localhost:8080/api/documents';

  // Lista com paginação e filtros
  list(page: number, size: number, title?: string, status?: string): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (title) params = params.set('title', title || '');
    if (status) params = params.set('status', status || '');

    return this.http.get<any>(this.API, { params });
  }

  createWithFile(title: string, file: File) {
    const formData = new FormData();
    formData.append('title', title);
    formData.append('file', file);
    return this.http.post(this.API, formData);
  }

  upload(id: number, file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<any>(`${this.API}/${id}/upload`, formData);
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.API}/${id}`);
  }
  download(id: number): Observable<Blob> {
    return this.http.get(`${this.API}/${id}/download`, {
      responseType: 'blob'
    });
  }

}
