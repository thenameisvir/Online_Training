package com.shopsphere.catalogservice.repository;

import com.shopsphere.catalogservice.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}