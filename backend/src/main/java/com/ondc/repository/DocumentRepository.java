package com.ondc.repository;

import com.ondc.entity.DocumentType;
import com.ondc.entity.UploadedDocument;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends MongoRepository<UploadedDocument, String> {
    List<UploadedDocument> findBySellerId(String sellerId);
    Optional<UploadedDocument> findBySellerIdAndDocumentType(String sellerId, DocumentType documentType);
    long countBySellerId(String sellerId);
    void deleteBySellerId(String sellerId);
}

