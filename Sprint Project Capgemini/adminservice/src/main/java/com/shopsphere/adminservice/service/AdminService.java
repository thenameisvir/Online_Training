package com.shopsphere.adminservice.service;

import com.shopsphere.adminservice.client.CatalogClient;
import com.shopsphere.adminservice.client.OrderClient;
import com.shopsphere.adminservice.dto.ProductRequest;
import com.shopsphere.adminservice.dto.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final CatalogClient catalogClient;
    private final OrderClient orderClient;

    // ─── DASHBOARD ──────────────────────────────

    public Map<String, Object> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        try {
            Map<String, Object> productPage = catalogClient.getAllProducts();
            dashboard.put("totalProducts", productPage.get("totalElements"));
        } catch (Exception e) {
            dashboard.put("totalProducts", "unavailable");
        }
        try {
            List<Object> orders = orderClient.getAllOrders();
            dashboard.put("totalOrders", orders.size());
        } catch (Exception e) {
            dashboard.put("totalOrders", "unavailable");
        }
        dashboard.put("message", "ShopSphere Admin Dashboard");
        return dashboard;
    }

    public Map<String, Object> getReports() {
        Map<String, Object> reports = new HashMap<>();
        try {
            Map<String, Object> productPage = catalogClient.getAllProducts();
            reports.put("totalProducts", productPage.get("totalElements"));
        } catch (Exception e) {
            reports.put("totalProducts", "unavailable");
        }
        try {
            List<Object> orders = orderClient.getAllOrders();
            reports.put("totalOrders", orders.size());
        } catch (Exception e) {
            reports.put("totalOrders", "unavailable");
        }
        return reports;
    }

    public ProductResponse createProduct(ProductRequest request, String role, String email) {
        try {
            return catalogClient.createProduct(request, role, email);
        } catch (Exception e) {
            throw new RuntimeException("Product create nahi ho paya: " + e.getMessage());
        }
    }

    public ProductResponse updateProduct(Long id, ProductRequest request, String role, String email) {
        try {
            return catalogClient.updateProduct(id, request, role, email);
        } catch (Exception e) {
            throw new RuntimeException("Product update nahi ho paya: " + e.getMessage());
        }
    }

    public Map<String, String> deleteProduct(Long id, String role, String email) {
        try {
            catalogClient.deleteProduct(id, role, email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Product deleted!");
            return response;
        } catch (Exception e) {
            throw new RuntimeException("Product delete nahi ho paya: " + e.getMessage());
        }
    }

    public List<Object> getAllOrders() {
        try {
            return orderClient.getAllOrders();
        } catch (Exception e) {
            throw new RuntimeException("Orders fetch nahi ho paye: " + e.getMessage());
        }
    }

    public Object updateOrderStatus(Long id, String status) {
        try {
            Map<String, String> body = new HashMap<>();
            body.put("status", status);
            return orderClient.updateOrderStatus(id, body);
        } catch (Exception e) {
            throw new RuntimeException("Order status update nahi ho paya: " + e.getMessage());
        }
    }

}