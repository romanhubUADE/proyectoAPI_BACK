package com.uade.tpo.Marketplace.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.Marketplace.entity.Order;
import com.uade.tpo.Marketplace.entity.dtos.OrderResponseDTO;
import com.uade.tpo.Marketplace.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    // --- GETs en DTO ---
    @GetMapping
    public List<OrderResponseDTO> list() {
        return service.findAllDto();
    }

    @GetMapping("/{id}")
    public OrderResponseDTO get(@PathVariable Long id) {
        return service.findDtoById(id);
    }

    @GetMapping("/by-user/{userId}")
    public List<OrderResponseDTO> byUser(@PathVariable Long userId) {
        return service.findDtosByUserId(userId);
    }

    // --- POST (si querés, luego también podés devolver DTO) ---
    @PostMapping
    public ResponseEntity<Order> create(@RequestBody Order body) {
        Order created = service.create(body);
        return ResponseEntity.created(URI.create("/api/orders/" + created.getId()))
                             .body(created);
    }
}
