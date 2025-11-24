package com.uade.tpo.Marketplace.service;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.uade.tpo.Marketplace.Exceptions.ProductDuplicateException;
import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.entity.ProductImage;
import com.uade.tpo.Marketplace.entity.dtos.ProductCreateDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductImageDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductResponseDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductUpdateDTO;
import com.uade.tpo.Marketplace.repository.CategoryRepository;
import com.uade.tpo.Marketplace.repository.ProductImageRepository;
import com.uade.tpo.Marketplace.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class ProductService {

    @Autowired private ProductRepository repo;
    @Autowired private CategoryRepository categoryRepo;
    @Autowired private ProductImageRepository productImageRepo;

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
                img.setData(f.getBytes());  
                img.setProduct(saved);
                productImageRepo.save(img);
            }
        }
        return toDto(saved);
    }


    private ProductResponseDTO toDto(Product p){
    List<ProductImageDTO> images = productImageRepo.findByProductId(p.getId())
            .stream().map(this::toImageDto).toList();

    Integer percent = getDiscountPercent(p);           // <- calcula % vigente
    String descuento = (percent != null && percent > 0)
            ? "descuento de " + percent + "%"
            : null;

    return new ProductResponseDTO(
            p.getId(), p.getName(), p.getDescription(), p.getPrice(), p.getStock(),
            p.getCategory()!=null ? p.getCategory().getId() : null,
            p.getCategory()!=null ? p.getCategory().getDescription() : null,
            p.getActivo(),
            images,
            descuento                                        // <- nuevo campo
    );
}

    private Integer getDiscountPercent(Product p) {
        
        return 0;  
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
    @Transactional
    public void addToStock(Long id, int delta) {
    if (delta == 0) return;
    int updated = repo.addToStock(id, delta); // usar 'repo'
    if (updated == 0) throw new EntityNotFoundException("producto no encontrado");

    Product p = repo.findById(id).orElseThrow(); // tipar como Product
    if (p.getStock() < 0) throw new IllegalStateException("stock no puede ser negativo");
}



    @Transactional(readOnly = true)
    public List<ProductResponseDTO> findAll() {
        return repo.findByActivoTrue().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
public List<ProductResponseDTO> findAllIncludingInactive() {
    return repo.findAll().stream().map(this::toDto).toList();
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