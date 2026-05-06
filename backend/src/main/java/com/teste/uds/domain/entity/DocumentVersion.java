package com.teste.uds.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "document_versions")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class DocumentVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(nullable = false)
    private String fileKey;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String contentType;

    @CreationTimestamp
    private LocalDateTime uploadedAt;

    @Column(nullable = false)
    private String uploadedBy;
}
