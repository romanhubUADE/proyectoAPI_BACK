package com.uade.tpo.Marketplace.repository;

import com.uade.tpo.Marketplace.entity.Order_Product;
import com.uade.tpo.Marketplace.entity.Order_Product_Id;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderProductRepository extends JpaRepository<Order_Product, Order_Product_Id> {}