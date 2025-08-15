package com.finman.controller;

import com.finman.model.Document;
import com.finman.model.User;
import com.finman.model.enums.DocumentType;
import com.finman.repository.DocumentRepository;
import com.finman.repository.UserRepository;
import com.finman.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
public class DocumentController {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    // Listar todos os documentos de um usuário
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Document>> getUserDocuments(@PathVariable Long userId) {
        List<Document> documents = documentRepository.findByUser_Id(userId);
        return ResponseEntity.ok(documents);
    }
    
    // Listar documentos por tipo
    @GetMapping("/user/{userId}/type/{documentType}")
    public ResponseEntity<List<Document>> getUserDocumentsByType(
            @PathVariable Long userId, 
            @PathVariable DocumentType documentType) {
        List<Document> documents = documentRepository.findByUser_IdAndDocumentType(userId, documentType);
        return ResponseEntity.ok(documents);
    }
    
    // Upload de documento
    @PostMapping("/user/{userId}")
    public ResponseEntity<String> uploadDocument(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            // Verificar se usuário existe
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            User user = userOpt.get();
            
            // Validar tipo de arquivo (apenas imagens e PDFs)
            String contentType = file.getContentType();
            if (contentType == null || 
                (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
                return ResponseEntity.badRequest().body("Apenas imagens e PDFs são permitidos");
            }
            
            // Validar tamanho (máximo 10MB)
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("Arquivo muito grande. Máximo 10MB");
            }
            
            // Salvar arquivo
            String fileName = fileStorageService.storeFile(file);
            
            // Criar documento
            Document document = new Document(user, documentType, fileName, 
                                           file.getOriginalFilename(), file.getSize(), contentType);
            if (description != null && !description.trim().isEmpty()) {
                document.setDescription(description);
            }
            
            documentRepository.save(document);
            
            return ResponseEntity.ok("Documento enviado com sucesso: " + fileName);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao enviar documento: " + e.getMessage());
        }
    }
    
    // Download de documento
    @GetMapping("/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long documentId) {
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Document document = documentOpt.get();
            byte[] fileBytes = fileStorageService.loadFileAsBytes(document.getFileName());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(document.getContentType()));
            headers.setContentLength(fileBytes.length);
            headers.setContentDispositionFormData("attachment", document.getOriginalFileName());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Visualizar documento (sem download)
    @GetMapping("/{documentId}/view")
    public ResponseEntity<byte[]> viewDocument(@PathVariable Long documentId) {
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Document document = documentOpt.get();
            System.out.println("Visualizando documento: " + document.getFileName() + " - Tipo: " + document.getContentType());
            
            byte[] fileBytes = fileStorageService.loadFileAsBytes(document.getFileName());
            System.out.println("Tamanho do arquivo: " + fileBytes.length + " bytes");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(document.getContentType()));
            headers.setContentLength(fileBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileBytes);
                    
        } catch (Exception e) {
            System.err.println("Erro ao visualizar documento: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // Verificar documento
    @PutMapping("/{documentId}/verify")
    public ResponseEntity<String> verifyDocument(
            @PathVariable Long documentId,
            @RequestParam("verifiedByUserId") Long verifiedByUserId) {
        
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Document document = documentOpt.get();
            document.verify(verifiedByUserId);
            documentRepository.save(document);
            
            return ResponseEntity.ok("Documento verificado com sucesso");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao verificar documento: " + e.getMessage());
        }
    }
    
    // Deletar documento
    @DeleteMapping("/{documentId}")
    public ResponseEntity<String> deleteDocument(@PathVariable Long documentId) {
        try {
            Optional<Document> documentOpt = documentRepository.findById(documentId);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Document document = documentOpt.get();
            
            // Deletar arquivo físico
            fileStorageService.deleteFile(document.getFileName());
            
            // Deletar registro do banco
            documentRepository.delete(document);
            
            return ResponseEntity.ok("Documento deletado com sucesso");
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro ao deletar documento: " + e.getMessage());
        }
    }
}
