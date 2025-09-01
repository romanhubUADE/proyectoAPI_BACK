package com.uade.tpo.Marketplace.repository;

import com.uade.tpo.Marketplace.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
