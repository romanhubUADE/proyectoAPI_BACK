package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.Exceptions.CategoryDuplicateException;
import com.uade.tpo.Marketplace.entity.Category;
import com.uade.tpo.Marketplace.entity.dtos.*;
import com.uade.tpo.Marketplace.repository.CategoryRepository;
import com.uade.tpo.Marketplace.repository.ProductRepository;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    private static final long GENERAL_CATEGORY_ID = 1L;

    @Autowired
    private CategoryRepository categoryRepo;

    @Autowired
    private ProductRepository productRepo;

    @PostConstruct
    public void initGeneralCategory() {
        if (categoryRepo.count() == 0) {
            Category general = new Category();
            general.setDescription("General");
            categoryRepo.save(general);  // la BD le dará id=1
        }
      }

      @Transactional(readOnly = true)
  public List<CategoryResponseDTO> findAll() {
    return categoryRepo.findAll().stream().map(this::toDto).toList();
  }

    @Transactional(readOnly = true)
  public CategoryResponseDTO findById(Long id) {
    var c = categoryRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found: " + id));
    return toDto(c);
  }

  public CategoryResponseDTO create(CategoryCreateDTO dto) throws CategoryDuplicateException {
    if (categoryRepo.existsByDescription(dto.description())) throw new CategoryDuplicateException();
    var c = new Category();
    c.setDescription(dto.description());
    return toDto(categoryRepo.save(c));
  }

  public void delete(Long id) {
    if (!categoryRepo.existsById(id))
      throw new ResponseStatusException(NOT_FOUND, "La categoria no existe: " + id);
    if (id == GENERAL_CATEGORY_ID)
      throw new ResponseStatusException(BAD_REQUEST, "No se puede borrar la categoria General");

    productRepo.reassignCategory(id, GENERAL_CATEGORY_ID);
    categoryRepo.deleteById(id);
  }

  private CategoryResponseDTO toDto(Category c) {
    return new CategoryResponseDTO(c.getId(), c.getDescription());
  }
}