package com.teste.uds.repository;

import com.teste.uds.domain.entity.Document;
import com.teste.uds.domain.enums.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Query("SELECT d FROM Document d WHERE " +
            "(:title IS NULL OR LOWER(d.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:status IS NULL OR d.status = :status)")
    Page<Document> findByFilters(@Param("title") String title,
                                 @Param("status") DocumentStatus status,
                                 Pageable pageable);

    @Query("SELECT d FROM Document d LEFT JOIN FETCH d.versions WHERE d.id = :id")
    Optional<Document> findByIdWithVersions(@Param("id") Long id);
}