package com.uade.tpo.Marketplace.controllers;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.tpo.Marketplace.Exceptions.CategoryDuplicateException;
import com.uade.tpo.Marketplace.entity.dtos.CategoryCreateDTO;
import com.uade.tpo.Marketplace.entity.dtos.CategoryResponseDTO;
import com.uade.tpo.Marketplace.service.CategoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoriesControllers {
    @Autowired
    private CategoryService categoryService;

    @GetMapping
  public List<CategoryResponseDTO> list() { return categoryService.findAll(); }

  @GetMapping("/{id}")
  public CategoryResponseDTO get(@PathVariable Long id) { return categoryService.findById(id); }

  @PreAuthorize("hasRole('ADMIN')")
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