package com.shopsphere.catalogservice.config;

import com.shopsphere.catalogservice.entity.Category;
import com.shopsphere.catalogservice.entity.Product;
import com.shopsphere.catalogservice.repository.CategoryRepository;
import com.shopsphere.catalogservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            if (categoryRepository.count() > 0) {
                log.info("Database already seeded with categories. Skipping initialization.");
                return;
            }

            log.info("Seeding database with 10 categories and 50 products...");

            String[] categories = {
                "Shoes", "Phones", "Laptops", "Watches", "Headphones",
                "Gaming", "Cameras", "Accessories", "Apparel", "Fragrances"
            };

            for (String catName : categories) {
                Category category = new Category();
                category.setName(catName);
                category = categoryRepository.save(category);

                for (int i = 1; i <= 5; i++) {
                    Product product = new Product();
                    product.setName(catName + " Product " + i);
                    product.setDescription("Premium high-quality " + catName.toLowerCase() + " for your lifestyle. Experience the best of Shop-Sphere.");
                    product.setPrice(99.99 + (i * 50));
                    product.setStock(10 + (i * 5));
                    product.setImageUrl("/assets/placeholders/product-placeholder.png");
                    product.setCategory(category);
                    productRepository.save(product);
                }
            }

            log.info("Successfully seeded 10 categories and 50 products!");
        };
    }
}
