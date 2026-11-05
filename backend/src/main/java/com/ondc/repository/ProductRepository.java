package com.ondc.repository;

import com.ondc.entity.Product;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findBySellerId(String sellerId);
    long countBySellerId(String sellerId);
    void deleteBySellerId(String sellerId);
}
