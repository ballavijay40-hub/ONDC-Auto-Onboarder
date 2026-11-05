package com.ondc.repository;

import com.ondc.entity.ComplianceRecord;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplianceRepository extends MongoRepository<ComplianceRecord, String> {
    Optional<ComplianceRecord> findTopBySellerIdOrderByCheckedAtDesc(String sellerId);
    void deleteBySellerId(String sellerId);
}

