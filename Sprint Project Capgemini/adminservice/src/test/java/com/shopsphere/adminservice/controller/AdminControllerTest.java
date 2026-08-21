package com.shopsphere.adminservice.controller;

import com.shopsphere.adminservice.dto.ProductRequest;
import com.shopsphere.adminservice.dto.ProductResponse;
import com.shopsphere.adminservice.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getDashboard_Success() throws Exception {
        when(adminService.getDashboard()).thenReturn(Map.of("totalProducts", 5));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(5));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_Success() throws Exception {
        ProductResponse response = new ProductResponse();
        response.setName("New Prod");
        when(adminService.createProduct(any(), anyString(), anyString())).thenReturn(response);

        mockMvc.perform(post("/admin/products")
                .with(csrf())
                .header("X-User-Role", "ADMIN")
                .header("X-User-Email", "admin@test.com")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"New Prod\", \"price\":100.0, \"description\":\"Luxe description\", \"categoryId\":1, \"imageUrl\":\"http://image.com\", \"stock\":10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Prod"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_Success() throws Exception {
        when(adminService.getAllOrders()).thenReturn(List.of(Map.of("id", 1)));

        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatus_Success() throws Exception {
        when(adminService.updateOrderStatus(anyLong(), anyString())).thenReturn(Map.of("status", "SHIPPED"));

        mockMvc.perform(put("/admin/orders/1/status")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"SHIPPED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));
    }

    @Test

    @WithMockUser(roles = "ADMIN")
    void getReports_Success() throws Exception {
        when(adminService.getReports()).thenReturn(Map.of("totalProducts", 10));

        mockMvc.perform(get("/admin/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProducts").value(10));
    }
}

