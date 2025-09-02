package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.entity.dtos.ProductCreateDTO;
import com.uade.tpo.Marketplace.repository.ProductRepository;
import com.uade.tpo.Marketplace.entity.Category;
import com.uade.tpo.Marketplace.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;

import java.util.List;

@Service
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository repo;

    @Autowired
    private CategoryRepository categoryRepo;


    @Transactional(readOnly = true)
    public List<Product> findAll() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public Product findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found: " + id));
    }

     public Product create(ProductCreateDTO dto){
        if (dto.categoryId() == null) {
        throw new ResponseStatusException(BAD_REQUEST, "categoryId es obligatorio");
    }
     Category category = categoryRepo.findById(dto.categoryId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found: " + dto.categoryId()));
        Product p = new Product();
        p.setName(dto.name());
        p.setDescription(dto.description());
        p.setPrice(dto.price());
        p.setStock(dto.stock());
        p.setCategory(category);
        return repo.save(p);
  }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "Product not found: " + id);
    repo.deleteById(id);
}

    public Product updateStock(Long id, int newStock) {
        if (newStock < 0) throw new ResponseStatusException(BAD_REQUEST, "stock no puede ser negativo");
        var db = findById(id);
        db.setStock(newStock);
    return repo.save(db);
}
}