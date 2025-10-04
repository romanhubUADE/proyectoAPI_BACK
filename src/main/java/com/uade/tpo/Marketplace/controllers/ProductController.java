package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.Exceptions.ProductDuplicateException;
import com.uade.tpo.Marketplace.entity.dtos.*;
import com.uade.tpo.Marketplace.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired private ProductService service;

    @GetMapping
    public List<ProductResponseDTO> list() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ProductResponseDTO get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductCreateDTO dto)
            throws ProductDuplicateException {
        var created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createWithImages(
            @RequestParam String name,
            @RequestParam String description,
            @RequestParam Double price,
            @RequestParam Integer stock,
            @RequestParam Long categoryId,
            @RequestParam("files") List<MultipartFile> files)
            throws IOException, ProductDuplicateException {

        var dto = service.createWithImages(name, description, price, stock, categoryId, files);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}")
    public ProductResponseDTO partialUpdate(@PathVariable Long id,
                                            @Valid @RequestBody ProductUpdateDTO dto) {
        return service.partialUpdate(id, dto);
    }

    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activar(@PathVariable Long id) {
        service.activar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> bajaLogica(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}