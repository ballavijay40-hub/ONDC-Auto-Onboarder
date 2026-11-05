package com.ondc.service;

import com.ondc.ai.AiServiceRouter;
import com.ondc.dto.request.AiGenerateRequest;
import com.ondc.dto.request.ProductCreateRequest;
import com.ondc.dto.response.AiCatalogResponse;
import com.ondc.dto.response.ProductResponse;
import com.ondc.entity.Product;
import com.ondc.entity.ProductSource;
import com.ondc.exception.ResourceNotFoundException;
import com.ondc.repository.ProductRepository;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final AiServiceRouter aiServiceRouter;

    public ProductResponse createProduct(String sellerId, ProductCreateRequest request) {
        Product product = new Product();
        product.setSellerId(sellerId);
        product.setProductName(request.getProductName().trim());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setBrand(request.getBrand());
        product.setColor(request.getColor());
        product.setSize(request.getSize());
        product.setGender(request.getGender());
        product.setMaterial(request.getMaterial());
        product.setImagePath(request.getImagePath());
        product.setImageContentType(request.getImageContentType());
        product.setTags(request.getTags() != null ? request.getTags() : List.of());
        product.setSource(request.getSource() != null ? request.getSource() : ProductSource.MANUAL);
        product.setCreatedAt(Instant.now());
        product.setUpdatedAt(Instant.now());

        Product saved = productRepository.save(product);
        log.info("Created product {} for seller {}", saved.getId(), sellerId);
        return toResponse(saved);
    }

    public AiCatalogResponse generateCatalog(AiGenerateRequest request) {
        return aiServiceRouter.generateCatalog(request);
    }

    public List<ProductResponse> getProductsBySeller(String sellerId) {
        return productRepository.findBySellerId(sellerId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return toResponse(product);
    }

    public void deleteProduct(String id, String sellerId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (sellerId != null && !sellerId.equals(product.getSellerId())) {
            throw new ResourceNotFoundException("Product does not belong to current seller");
        }
        productRepository.delete(product);
    }

    public ProductResponse toResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .sellerId(p.getSellerId())
                .productName(p.getProductName())
                .description(p.getDescription())
                .category(p.getCategory())
                .tags(p.getTags())
                .imagePath(p.getImagePath())
                .imageContentType(p.getImageContentType())
                .price(p.getPrice())
                .brand(p.getBrand())
                .color(p.getColor())
                .size(p.getSize())
                .gender(p.getGender())
                .material(p.getMaterial())
                .source(p.getSource())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
