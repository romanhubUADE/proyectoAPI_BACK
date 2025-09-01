package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository repo;
    public ProductService(ProductRepository repo) { this.repo = repo; }

    @Transactional(readOnly = true)
    public List<Product> findAll() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Product not found: " + id));
    }

    public Product create(Product p) {
        p.setId(null);
        return repo.save(p);
    }

    public Product update(Long id, Product p) {
        Product db = findById(id);
        db.setName(p.getName());
        db.setPrice(p.getPrice());
        db.setStock(p.getStock());
        return repo.save(db);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Product not found: " + id);
        repo.deleteById(id);
    }

    public Product updateStock(Long id, int newStock) {
        Product db = findById(id);
        db.setStock(newStock);
        return repo.save(db);
    }
}