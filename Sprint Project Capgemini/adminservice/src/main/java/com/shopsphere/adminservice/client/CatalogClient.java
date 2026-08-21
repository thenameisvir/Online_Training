package com.shopsphere.adminservice.client;

import com.shopsphere.adminservice.config.FeignConfig;
import com.shopsphere.adminservice.dto.ProductRequest;
import com.shopsphere.adminservice.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@FeignClient(name = "catalogservice")
public interface CatalogClient {

    @GetMapping("/catalog/products")
    Map<String, Object> getAllProducts();

    @PostMapping("/catalog/products")
    ProductResponse createProduct(
            @RequestBody ProductRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email);

    @PutMapping("/catalog/products/{id}")
    ProductResponse updateProduct(
            @PathVariable Long id,
            @RequestBody ProductRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email);

    @DeleteMapping("/catalog/products/{id}")
    void deleteProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email);
}
