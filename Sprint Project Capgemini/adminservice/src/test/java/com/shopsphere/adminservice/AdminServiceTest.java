package com.shopsphere.adminservice;

import com.shopsphere.adminservice.client.CatalogClient;
import com.shopsphere.adminservice.client.OrderClient;
import com.shopsphere.adminservice.dto.OrderResponse;
import com.shopsphere.adminservice.dto.ProductRequest;
import com.shopsphere.adminservice.dto.ProductResponse;
import com.shopsphere.adminservice.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Admin Service Tests")
class AdminServiceTest {

    @Mock
    private CatalogClient catalogClient;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private AdminService adminService;

    private static final String ADMIN_ROLE = "ADMIN";
    private static final String ADMIN_EMAIL = "admin@shopsphere.com";

    @Test
    @DisplayName("Dashboard returns product count and order count")
    void getDashboard_Success() {
        Map<String, Object> productPage = new HashMap<>();
        productPage.put("totalElements", 5);

        when(catalogClient.getAllProducts()).thenReturn(productPage);
        when(orderClient.getAllOrders()).thenReturn(List.of(new Object(), new Object()));

        Map<String, Object> dashboard = adminService.getDashboard();

        assertNotNull(dashboard);
        assertEquals(5, dashboard.get("totalProducts"));
        assertEquals(2, dashboard.get("totalOrders"));
        assertEquals("ShopSphere Admin Dashboard", dashboard.get("message"));
    }

    @Test
    @DisplayName("Dashboard handles catalog service failure gracefully")
    void getDashboard_CatalogDown_ReturnsUnavailable() {
        when(catalogClient.getAllProducts()).thenThrow(new RuntimeException("Connection refused"));
        when(orderClient.getAllOrders()).thenReturn(List.of());

        Map<String, Object> dashboard = adminService.getDashboard();

        assertEquals("unavailable", dashboard.get("totalProducts"));
        assertEquals(0, dashboard.get("totalOrders"));
    }

    @Test
    @DisplayName("Create product delegates to catalog client with auth headers")
    void createProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Test Product");
        request.setPrice(999.0);

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Test Product");

        when(catalogClient.createProduct(any(ProductRequest.class), eq(ADMIN_ROLE), eq(ADMIN_EMAIL)))
                .thenReturn(mockResponse);

        ProductResponse result = adminService.createProduct(request, ADMIN_ROLE, ADMIN_EMAIL);

        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(catalogClient).createProduct(any(), eq(ADMIN_ROLE), eq(ADMIN_EMAIL));
    }

    @Test
    @DisplayName("Create product throws when catalog client fails")
    void createProduct_Failure_ThrowsException() {
        ProductRequest request = new ProductRequest();
        when(catalogClient.createProduct(any(), anyString(), anyString()))
                .thenThrow(new RuntimeException("Service down"));

        assertThrows(RuntimeException.class,
                () -> adminService.createProduct(request, ADMIN_ROLE, ADMIN_EMAIL));
    }

    @Test
    @DisplayName("Update product delegates to catalog client")
    void updateProduct_Success() {
        ProductRequest request = new ProductRequest();
        request.setName("Updated Product");

        ProductResponse mockResponse = new ProductResponse();
        mockResponse.setId(1L);
        mockResponse.setName("Updated Product");

        when(catalogClient.updateProduct(eq(1L), any(), eq(ADMIN_ROLE), eq(ADMIN_EMAIL)))
                .thenReturn(mockResponse);

        ProductResponse result = adminService.updateProduct(1L, request, ADMIN_ROLE, ADMIN_EMAIL);

        assertEquals("Updated Product", result.getName());
    }

    @Test
    @DisplayName("Delete product returns success message")
    void deleteProduct_Success() {
        doNothing().when(catalogClient).deleteProduct(eq(1L), eq(ADMIN_ROLE), eq(ADMIN_EMAIL));

        Map<String, String> result = adminService.deleteProduct(1L, ADMIN_ROLE, ADMIN_EMAIL);

        assertEquals("Product deleted!", result.get("message"));
        verify(catalogClient).deleteProduct(1L, ADMIN_ROLE, ADMIN_EMAIL);
    }

    @Test
    @DisplayName("Get all orders returns list from order client")
    void getAllOrders_Success() {
        when(orderClient.getAllOrders()).thenReturn(List.of(new Object(), new Object(), new Object()));

        List<Object> orders = adminService.getAllOrders();

        assertEquals(3, orders.size());
    }

    @Test
    @DisplayName("Update order status delegates to order client")
    void updateOrderStatus_Success() {
        Map<String, String> statusBody = new HashMap<>();
        statusBody.put("status", "SHIPPED");

        OrderResponse mockResponse = new OrderResponse();
        mockResponse.setStatus("SHIPPED");

        when(orderClient.updateOrderStatus(eq(1L), any(Map.class))).thenReturn(mockResponse);

        Object result = adminService.updateOrderStatus(1L, "SHIPPED");

        assertNotNull(result);
        verify(orderClient).updateOrderStatus(eq(1L), any(Map.class));
    }


    @Test
    @DisplayName("Reports return product and order counts")
    void getReports_Success() {
        Map<String, Object> productPage = new HashMap<>();
        productPage.put("totalElements", 10);
        when(catalogClient.getAllProducts()).thenReturn(productPage);
        when(orderClient.getAllOrders()).thenReturn(List.of(new Object()));

        Map<String, Object> reports = adminService.getReports();

        assertEquals(10, reports.get("totalProducts"));
        assertEquals(1, reports.get("totalOrders"));
    }
}