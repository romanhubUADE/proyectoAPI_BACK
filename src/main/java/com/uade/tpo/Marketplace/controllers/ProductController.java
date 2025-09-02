package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.entity.dtos.ProductCreateDTO;
import com.uade.tpo.Marketplace.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
/*import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;*/

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService service;

    @GetMapping public List<Product> list() { return service.findAll(); }
    @GetMapping("/{id}") public Product get(@PathVariable Long id) { return service.findById(id); }

    @org.springframework.web.bind.annotation.PostMapping
  public org.springframework.http.ResponseEntity<com.uade.tpo.Marketplace.entity.Product>
  create(@jakarta.validation.Valid @org.springframework.web.bind.annotation.RequestBody
         com.uade.tpo.Marketplace.entity.dtos.ProductCreateDTO dto) {
    var created = service.create(dto);
    return org.springframework.http.ResponseEntity
        .created(java.net.URI.create("/api/products/" + created.getId()))
        .body(created);
  }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock/{value}")
        public Product updateStock(@PathVariable @Min(1) Long id, @PathVariable int value) {
    return service.updateStock(id, value);
}
}