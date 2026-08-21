package com.shopsphere.catalogservice;

import com.shopsphere.catalogservice.exception.CategoryNotFoundException;
import com.shopsphere.catalogservice.exception.ProductNotFoundException;
import com.shopsphere.catalogservice.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleProductNotFound_ReturnsNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException("Product Not Found");
        ResponseEntity<Map<String, Object>> response = handler.handleProductNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Product Not Found", response.getBody().get("message"));
    }

    @Test
    void handleCategoryNotFound_ReturnsNotFound() {
        CategoryNotFoundException ex = new CategoryNotFoundException("Category Not Found");
        ResponseEntity<Map<String, Object>> response = handler.handleCategoryNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Category Not Found", response.getBody().get("message"));
    }
}