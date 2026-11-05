package com.ondc.controller;

import com.ondc.dto.request.AiGenerateRequest;
import com.ondc.dto.request.ProductCreateRequest;
import com.ondc.dto.response.AiCatalogResponse;
import com.ondc.dto.response.ProductResponse;
import com.ondc.service.ProductService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductCreateRequest request,
            Principal principal) {
        String sellerId = principal.getName();
        ProductResponse response = productService.createProduct(sellerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/generate")
    public ResponseEntity<AiCatalogResponse> generateCatalog(
            @Valid @RequestBody AiGenerateRequest request) {
        AiCatalogResponse response = productService.generateCatalog(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seller/{sellerId}")
    public ResponseEntity<List<ProductResponse>> getProductsBySeller(@PathVariable String sellerId) {
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ProductResponse>> getMyProducts(Principal principal) {
        String sellerId = principal.getName();
        return ResponseEntity.ok(productService.getProductsBySeller(sellerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id, Principal principal) {
        String sellerId = principal.getName();
        productService.deleteProduct(id, sellerId);
        return ResponseEntity.noContent().build();
    }
}
