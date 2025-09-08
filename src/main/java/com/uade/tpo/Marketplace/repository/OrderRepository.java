package com.uade.tpo.Marketplace.repository;

import com.uade.tpo.Marketplace.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
     List<Order> findAllByUser_Id(Long userId);    
   List<Order> findAllByUserEmail(String email); 
}