package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.entity.Order;
import com.uade.tpo.Marketplace.service.OrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    @Autowired
    private OrderService service;

  @GetMapping public List<Order> list(){ return service.findAll(); }
  @GetMapping("/{id}") public Order get(@PathVariable Long id){ return service.findById(id); }
  @GetMapping("/by-user/{userId}") public List<Order> byUser(@PathVariable Long userId){ return service.findByUserId(userId); }
  @PostMapping public ResponseEntity<Order> create(@RequestBody Order body){
    Order created = service.create(body);
    return ResponseEntity.created(URI.create("/api/orders/"+created.getId())).body(created);
  }
  
}