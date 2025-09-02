package com.uade.tpo.Marketplace.repository;

import com.uade.tpo.Marketplace.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}