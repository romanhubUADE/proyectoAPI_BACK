package com.uade.tpo.Marketplace.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.uade.tpo.Marketplace.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    // A) sumar stock
    @Modifying
    @Query("update Product p set p.stock = p.stock + :delta where p.id = :id")
    int addToStock(@Param("id") Long id, @Param("delta") int delta);

    // reasignar categoría
    @Modifying
    @Query("update Product p set p.category.id = :newId where p.category.id = :oldId")
    int reassignCategory(@Param("oldId") Long oldId, @Param("newId") Long newId);

    List<Product> findByActivoTrue();
}
