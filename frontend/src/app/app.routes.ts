import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { 
    path: 'documents', 
    loadComponent: () => import('./features/documents/document-list/document-list')
      .then(m => m.DocumentListComponent),
    canActivate: [authGuard]
  },
  { path: '**', redirectTo: 'login' }
];