package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.Exceptions.CategoryDuplicateException;
import com.uade.tpo.Marketplace.entity.Category;
import com.uade.tpo.Marketplace.entity.dtos.CategoryCreateDTO;
import com.uade.tpo.Marketplace.entity.dtos.CategoryResponseDTO;
import com.uade.tpo.Marketplace.service.CategoryService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoriesControllers {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
  public List<CategoryResponseDTO> list() { return categoryService.findAll(); }

  @GetMapping("/{id}")
  public CategoryResponseDTO get(@PathVariable Long id) { return categoryService.findById(id); }

  @PostMapping
  public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CategoryCreateDTO dto)
      throws CategoryDuplicateException {
    var created = categoryService.create(dto);
    return ResponseEntity.created(URI.create("/api/categories/" + created.id())).body(created);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}