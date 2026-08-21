package com.shopsphere.catalogservice;

import com.shopsphere.catalogservice.dto.ProductRequest;
import com.shopsphere.catalogservice.dto.ProductResponse;
import com.shopsphere.catalogservice.entity.Category;
import com.shopsphere.catalogservice.entity.Product;
import com.shopsphere.catalogservice.exception.CategoryNotFoundException;
import com.shopsphere.catalogservice.exception.ProductNotFoundException;
import com.shopsphere.catalogservice.repository.CategoryRepository;
import com.shopsphere.catalogservice.repository.ProductRepository;
import com.shopsphere.catalogservice.service.CatalogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks private CatalogService catalogService;

    private Category electronics;
    private Product laptop;

    @BeforeEach
    void setup() {
        electronics = new Category();
        electronics.setId(1L);
        electronics.setName("Electronics");

        laptop = new Product();
        laptop.setId(10L);
        laptop.setName("Gaming Laptop");
        laptop.setPrice(75000.0);
        laptop.setStock(50);
        laptop.setCategory(electronics);
    }

    @Test
    void getAllProducts_Success() {
        Page<Product> page = new PageImpl<>(List.of(laptop));
        when(productRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ProductResponse> result = catalogService.getAllProducts(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Gaming Laptop", result.getContent().get(0).getName());
    }

    @Test
    void updateProduct_CategoryNotFound_ThrowsException() {
        ProductRequest req = new ProductRequest();
        req.setCategoryId(99L);
        when(productRepository.findById(10L)).thenReturn(Optional.of(laptop));
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () -> catalogService.updateProduct(10L, req));
    }

    @Test
    void getAllCategories_Success() {
        when(categoryRepository.findAll()).thenReturn(List.of(electronics));
        List<Category> result = catalogService.getAllCategories();
        assertEquals(1, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void mapToResponse_CategoryNull_Success() {
        laptop.setCategory(null);
        when(productRepository.findById(10L)).thenReturn(Optional.of(laptop));

        ProductResponse res = catalogService.getProductById(10L);

        assertNull(res.getCategoryName());
    }

    @Test
    void getProductById_Success() {
        when(productRepository.findById(10L)).thenReturn(Optional.of(laptop));
        ProductResponse res = catalogService.getProductById(10L);
        assertEquals("Gaming Laptop", res.getName());
    }

    @Test
    void getProductById_NotFound_ThrowsException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> catalogService.getProductById(99L));
    }

    @Test
    void updateStock_Success() {
        when(productRepository.findById(10L)).thenReturn(Optional.of(laptop));
        // Mocking save to return the same product instance
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse res = catalogService.updateStock(10L, 5);

        assertEquals(45, res.getStock());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateStock_InsufficientStock_ThrowsException() {
        laptop.setStock(3);
        when(productRepository.findById(10L)).thenReturn(Optional.of(laptop));
        assertThrows(RuntimeException.class, () -> catalogService.updateStock(10L, 5));
    }

    @Test
    void getFeaturedProducts_Success() {
        when(productRepository.findAll()).thenReturn(List.of(laptop));
        List<ProductResponse> featured = catalogService.getFeaturedProducts();
        assertFalse(featured.isEmpty());
        assertTrue(featured.size() <= 6);
    }

    @Test
    void createProduct_Success() {
        ProductRequest req = new ProductRequest();
        req.setName("Gaming Laptop");
        req.setCategoryId(1L);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(electronics));
        when(productRepository.save(any(Product.class))).thenReturn(laptop);

        ProductResponse res = catalogService.createProduct(req);

        assertNotNull(res);
        verify(productRepository).save(any());
    }

    @Test
    void searchProducts_Success() {
        Page<Product> productPage = new PageImpl<>(List.of(laptop));
        when(productRepository.findByNameContainingIgnoreCase(anyString(), any(Pageable.class)))
                .thenReturn(productPage);

        Page<ProductResponse> results = catalogService.searchProducts("Laptop", 0, 10);

        assertEquals(1, results.getTotalElements());
        assertEquals("Gaming Laptop", results.getContent().get(0).getName());
    }

    @Test
    void getProductsByCategory_Success() {
        Page<Product> page = new PageImpl<>(List.of(laptop));
        when(productRepository.findByCategoryId(anyLong(), any(Pageable.class))).thenReturn(page);

        Page<ProductResponse> result = catalogService.getProductsByCategory(1L, 0, 10);

        assertEquals(1, result.getTotalElements());
        verify(productRepository).findByCategoryId(eq(1L), any(Pageable.class));
    }

    @Test
    void deleteProduct_Success() {
        doNothing().when(productRepository).deleteById(10L);
        catalogService.deleteProduct(10L);
        verify(productRepository, times(1)).deleteById(10L);
    }

    @Test
    void createCategory_Success() {
        when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));
        Category result = catalogService.createCategory("Shoes");
        assertEquals("Shoes", result.getName());
    }
}