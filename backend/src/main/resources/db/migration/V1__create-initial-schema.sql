-- Criação da tabela de usuários
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL
) ENGINE=InnoDB;

-- Inserção de usuário ADMIN padrão (Senha: 'admin123' em BCrypt)
INSERT INTO users (username, password, role)
VALUES ('admin', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5f2LPNLqn.M5T.zVw0x9vA/7tXU992', 'ADMIN');

-- Criação da tabela de documentos
CREATE TABLE documents (
                           id BIGINT AUTO_INCREMENT PRIMARY KEY,
                           title VARCHAR(255) NOT NULL,
                           description TEXT,
                           owner VARCHAR(255) NOT NULL,
                           status VARCHAR(20) NOT NULL,
                           created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- Tabela de tags (ElementCollection)
CREATE TABLE document_tags (
                               document_id BIGINT NOT NULL,
                               tag VARCHAR(255) NOT NULL,
                               CONSTRAINT fk_document_tags FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Tabela de versões (Histórico de uploads)
CREATE TABLE document_versions (
                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                   document_id BIGINT NOT NULL,
                                   file_key VARCHAR(255) NOT NULL,
                                   file_name VARCHAR(255) NOT NULL,
                                   content_type VARCHAR(100) NOT NULL,
                                   uploaded_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   uploaded_by VARCHAR(255) NOT NULL,
                                   CONSTRAINT fk_document_versions FOREIGN KEY (document_id) REFERENCES documents(id) ON DELETE CASCADE
) ENGINE=InnoDB;
