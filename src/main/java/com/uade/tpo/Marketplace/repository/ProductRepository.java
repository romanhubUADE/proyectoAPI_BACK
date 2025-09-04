package com.uade.tpo.Marketplace.repository;

import com.uade.tpo.Marketplace.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);

    @Modifying
    @Query("update Product p set p.category.id = :newId where p.category.id = :oldId")
    void reassignCategory(@Param("oldId") Long oldId, @Param("newId") Long newId);
}
