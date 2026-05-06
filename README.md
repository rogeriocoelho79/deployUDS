# GED / Documentos

Este projeto é uma plataforma para gerenciamento de documentos, permitindo upload de arquivos, controle de versões, filtros avançados e autenticação segura via JWT.

## 🛠️ Stack Tecnológica

### Backend
- **Java 17 / Spring Boot 4.0.6**: Arquitetura modular e performance otimizada.
- **Spring Security + JWT**: Autenticação stateless com controle de roles.
- **Spring Data JPA + MySQL 8.0**: Persistência de dados com Flyway Migrations.

### Frontend
- **Angular 18**: Gerenciamento de estado com **Signals** e Standalone Components.
- **Nginx**: Servidor web de alta performance para hospedar a aplicação Single Page (SPA).

---

## 🚀 Como Executar (Docker Compose)

O projeto está totalmente "dockerizado", garantindo que todas as dependências subam de forma integrada.

### 1. Pré-requisitos
- Docker e Docker Compose instalados.

### 2. Startup do Ambiente
Na raiz do projeto, execute o comando abaixo:
```bash
docker-compose up --build
```

### 3. Acesso às Aplicações
- **Frontend (Web)**: [http://localhost:4200](http://localhost:4200)
- **Backend (API)**: [http://localhost:8080](http://localhost:8080)
- **Documentação (Swagger)**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 🔐 Credenciais de Acesso
O banco de dados é inicializado com um usuário administrativo padrão:
- **Usuário:** `admin`
- **Senha:** `admin123`

---

## ⚙️ Configuração do Servidor Nginx (Frontend)

Para suportar o roteamento do Angular (SPA) dentro do container, foi implementada uma configuração customizada do Nginx (`nginx.conf`):

```nginx
server {
    listen 80;
    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
        try_files $uri $uri/ /index.html =404;
    }
}
```
*Esta configuração garante que requisições para rotas internas (ex: `/documents`) sejam redirecionadas para o `index.html`.*

---

## 📝 Decisões de Arquitetura

1. **Multi-stage Builds**: Os Dockerfiles do Backend e Frontend utilizam multi-stage builds para reduzir drasticamente o tamanho das imagens finais, separando o ambiente de compilação do ambiente de execução.
2. **Healthcheck Orchestration**: O backend possui uma dependência de saúde (`service_healthy`) do banco de dados MySQL no `docker-compose.yml`, evitando falhas de conexão durante a inicialização.
3. **Persistência Volumétrica**:
    - `/var/lib/mysql`: Volume para os dados do banco.
    - `/app/uploads`: Volume para os arquivos físicos enviados, garantindo que não se percam ao reiniciar o container.
4. **Segurança**: Interceptor Angular configurado para anexar o Bearer Token em todas as chamadas HTTP de forma transparente.

---

### 📂 Estrutura de Pastas
- `/backend`: API RESTful Spring Boot.
- `/frontend`: SPA Angular com Dockerfile (Nginx).
- `/uploads`: Diretório de persistência de arquivos.
