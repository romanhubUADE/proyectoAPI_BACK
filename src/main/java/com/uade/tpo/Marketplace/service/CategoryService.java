package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Category;
import com.uade.tpo.Marketplace.repository.CategoryRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryRepository repo;

    @Transactional(readOnly = true)
    public List<Category> getCategories() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        return repo.findById(categoryId)
                   .orElseThrow(() -> new IllegalArgumentException("Category not found: " + categoryId));
    }

    public Category createCategory(Category entity) {
        // si usás ID autogenerado, asegurate de nullificarlo:
        // entity.setId(0);
        return repo.save(entity);
        
    }
    public void deleteCategory(long categoryId) {
    if (!repo.existsById(categoryId)) {
        throw new IllegalArgumentException("Category not found: " + categoryId);
    }
    repo.deleteById(categoryId);
}
}