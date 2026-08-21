package com.shopsphere.adminservice.controller;

import com.shopsphere.adminservice.dto.ProductRequest;
import com.shopsphere.adminservice.dto.ProductResponse;
import com.shopsphere.adminservice.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // ─── DASHBOARD ──────────────────────────────
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    // ─── PRODUCTS ───────────────────────────────

    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(adminService.createProduct(request, role, email));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(adminService.updateProduct(id, request, role, email));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @PathVariable Long id,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Email") String email) {
        return ResponseEntity.ok(adminService.deleteProduct(id, role, email));
    }

    // ─── ORDERS ─────────────────────────────────

    @GetMapping("/orders")
    public ResponseEntity<List<Object>> getAllOrders() {
        return ResponseEntity.ok(adminService.getAllOrders());
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<Object> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(adminService.updateOrderStatus(id, body.get("status")));
    }

    // ─── REPORTS ────────────────────────────────

    @GetMapping("/reports")
    public ResponseEntity<Map<String, Object>> getReports() {
        return ResponseEntity.ok(adminService.getReports());
    }
}