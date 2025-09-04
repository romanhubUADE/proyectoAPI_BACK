package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.entity.dtos.ProductCreateDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductResponseDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductUpdateDTO;
import com.uade.tpo.Marketplace.repository.ProductRepository;
import com.uade.tpo.Marketplace.Exceptions.ProductDuplicateException;
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

    private ProductResponseDTO toDto(Product p){
    return new ProductResponseDTO(
        p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(),
        p.getCategory()!=null ? p.getCategory().getId() : null,
        p.getCategory()!=null ? p.getCategory().getDescription() : null
    );
  }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll(){
        return repo.findAll().stream().map(this::toDto).toList();
  }


    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id){
        var p = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "El producto no existe: " + id));
        return toDto(p);
    }

    public ProductResponseDTO create(ProductCreateDTO dto) throws ProductDuplicateException{
        if (repo.existsByName(dto.name())) throw new ProductDuplicateException();  // dup por nombre :contentReference[oaicite:0]{index=0}
            var category = categoryRepo.findById(dto.categoryId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found: " + dto.categoryId()));
        var p = new Product();
            p.setName(dto.name());
            p.setDescription(dto.description());
            p.setPrice(dto.price());
            p.setStock(dto.stock());
            p.setCategory(category);
        return toDto(repo.save(p));
  }
    public ProductResponseDTO partialUpdate(Long id, ProductUpdateDTO dto) {
        var p = repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "El producto no existe: " + id));

        if (dto.description() != null) p.setDescription(dto.description());
        if (dto.price() != null) {
            if (dto.price() <= 0) throw new ResponseStatusException(BAD_REQUEST, "price debe ser > 0");
            p.setPrice(dto.price());
        }
        if (dto.stock() != null) {
            if (dto.stock() < 0) throw new ResponseStatusException(BAD_REQUEST, "stock no puede ser negativo");
            p.setStock(dto.stock());
    }

    return toDto(repo.save(p));
}

    public void delete(Long id){
        if (!repo.existsById(id)) throw new ResponseStatusException(NOT_FOUND, "El producto no existe: " + id);
    repo.deleteById(id);
  }

}