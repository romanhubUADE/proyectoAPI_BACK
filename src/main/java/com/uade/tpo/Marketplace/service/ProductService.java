package com.uade.tpo.Marketplace.service;
import com.uade.tpo.Marketplace.Exceptions.ProductDuplicateException;
import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.entity.ProductImage;
import com.uade.tpo.Marketplace.entity.dtos.*;
import com.uade.tpo.Marketplace.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
public class ProductService {

    @Autowired private ProductRepository repo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private ProductImageRepository productImageRepo;

    // ---------- NUEVO: crear producto + imágenes ----------
    public ProductResponseDTO createWithImages(String name, String description, Double price,
                                               Integer stock, Long categoryId,
                                               List<MultipartFile> files)
            throws IOException, ProductDuplicateException {

        if (repo.existsByName(name)) throw new ProductDuplicateException();

        var category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Category not found: " + categoryId));

        var p = new Product();
        p.setName(name);
        p.setDescription(description);
        p.setPrice(price);
        p.setStock(stock);
        p.setCategory(category);

        var saved = repo.save(p);

        if (files != null) {
            for (MultipartFile f : files) {
                if (f == null || f.isEmpty()) continue;
                var img = new ProductImage();
                img.setFileName(f.getOriginalFilename());
                img.setContentType(f.getContentType());
                img.setData(f.getBytes());   // BLOB
                img.setProduct(saved);
                productImageRepo.save(img);
            }
        }
        return toDto(saved);
    }

    // ---------- existentes ----------
    private ProductResponseDTO toDto(Product p){
        List<ProductImageDTO> images = productImageRepo.findByProductId(p.getId())
                .stream().map(this::toImageDto).toList();

        return new ProductResponseDTO(
                p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(),
                p.getCategory()!=null ? p.getCategory().getId() : null,
                p.getCategory()!=null ? p.getCategory().getDescription() : null,
                p.getActivo(),
                images
        );
    }

    private ProductImageDTO toImageDto(ProductImage img) {
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/products/")
                .path(img.getProduct().getId().toString())
                .path("/images/")
                .path(img.getId().toString())
                .toUriString();

        return new ProductImageDTO(
                img.getId(),
                img.getFileName(),
                img.getContentType(),
                url
        );
    }

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return repo.findByActivoTrue().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public ProductResponseDTO findById(Long id){
        var p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "El producto no existe: " + id));
        return toDto(p);
    }

    public ProductResponseDTO create(ProductCreateDTO dto) throws ProductDuplicateException{
        if (repo.existsByName(dto.name())) throw new ProductDuplicateException();
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

    public void delete(Long id) {
        var p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "El producto no existe: " + id));
        p.setActivo(false);
        repo.save(p);
    }

    public void activar(Long id) {
        var p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "El producto no existe: " + id));
        p.setActivo(true);
        repo.save(p);
    }
}