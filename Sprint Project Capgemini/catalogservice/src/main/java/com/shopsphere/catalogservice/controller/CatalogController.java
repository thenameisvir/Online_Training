package com.shopsphere.catalogservice.controller;

import com.shopsphere.catalogservice.dto.ProductRequest;
import com.shopsphere.catalogservice.dto.ProductResponse;
import com.shopsphere.catalogservice.entity.Category;
import com.shopsphere.catalogservice.service.CatalogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogService catalogService;

    @GetMapping("/catalog/products")
    public ResponseEntity<Page<ProductResponse>> getAllProducts(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (search != null) {
            // Search ke liye bhi pagination (Maine Service mein add kiya hai niche)
            return ResponseEntity.ok(catalogService.searchProducts(search, page, size));
        }
        if (categoryId != null) {
            return ResponseEntity.ok(catalogService.getProductsByCategory(categoryId, page, size));
        }
        return ResponseEntity.ok(catalogService.getAllProducts(page, size));
    }


    @GetMapping("/catalog/products/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.getProductById(id));
    }

    @GetMapping("/catalog/featured")
    public ResponseEntity<List<ProductResponse>> getFeatured() {
        return ResponseEntity.ok(catalogService.getFeaturedProducts());
    }

    @PutMapping("/catalog/products/{id}/stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'INTERNAL')")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> body) {
        Integer quantity = body.get("quantity");
        if (quantity == null) {
            throw new IllegalArgumentException("quantity field is required");
        }
        return ResponseEntity.ok(
                catalogService.updateStock(id, quantity));
    }
    @PostMapping("/catalog/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.ok(catalogService.createProduct(request));
    }

    @PutMapping("/catalog/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(catalogService.updateProduct(id, request));
    }

    @DeleteMapping("/catalog/products/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProduct(
            @PathVariable Long id) {
        catalogService.deleteProduct(id);
        return ResponseEntity.ok(Map.of("message", "Product deleted!"));
    }



    @GetMapping("/catalog/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(catalogService.getAllCategories());
    }

    @PostMapping("/catalog/categories")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Category> createCategory(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name field is required");
        }
        return ResponseEntity.ok(catalogService.createCategory(name));
    }
}