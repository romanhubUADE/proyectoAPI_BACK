package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    public ProductController(ProductService service) { this.service = service; }

    @GetMapping public List<Product> list() { return service.findAll(); }
    @GetMapping("/{id}") public Product get(@PathVariable Long id) { return service.findById(id); }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product body) {
        Product created = service.create(body);
        return ResponseEntity.created(URI.create("/api/products/" + created.getId())).body(created);
    }

    @PutMapping("/{id}") public Product update(@PathVariable Long id, @RequestBody Product body) {
        return service.update(id, body);
    }

    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/stock/{value}") public Product updateStock(@PathVariable Long id, @PathVariable int value) {
        return service.updateStock(id, value);
    }
}