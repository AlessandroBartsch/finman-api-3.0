package com.finman.repository;

import com.finman.model.Document;
import com.finman.model.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByUserId(Long userId);
    
    List<Document> findByUserIdAndDocumentType(Long userId, DocumentType documentType);
    
    List<Document> findByUserIdAndIsVerified(Long userId, Boolean isVerified);
    
    Optional<Document> findByUserIdAndDocumentTypeAndIsVerified(Long userId, DocumentType documentType, Boolean isVerified);
    
    long countByUserIdAndDocumentType(Long userId, DocumentType documentType);
}
