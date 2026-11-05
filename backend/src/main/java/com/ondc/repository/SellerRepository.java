package com.ondc.repository;

import com.ondc.entity.Seller;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends MongoRepository<Seller, String> {
    Optional<Seller> findByEmail(String email);
    boolean existsByEmail(String email);
}
