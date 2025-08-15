package com.finman.repository;

import com.finman.model.Document;
import com.finman.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByUser_Id(Long userId);
    
    List<Document> findByUser_IdAndDocumentType(Long userId, DocumentType documentType);
    
    List<Document> findByUser_IdAndIsVerified(Long userId, Boolean isVerified);
    
    Optional<Document> findByUser_IdAndDocumentTypeAndIsVerified(Long userId, DocumentType documentType, Boolean isVerified);
    
    long countByUser_IdAndDocumentType(Long userId, DocumentType documentType);
}
