package com.uade.tpo.Marketplace.controllers;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.uade.tpo.Marketplace.Exceptions.ProductDuplicateException;
import com.uade.tpo.Marketplace.entity.dtos.ProductCreateDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductResponseDTO;
import com.uade.tpo.Marketplace.entity.dtos.ProductUpdateDTO;
import com.uade.tpo.Marketplace.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    public ProductController(ProductService service){ this.service = service; }

    // D) mensaje si no hay productos
    @GetMapping
    public ResponseEntity<Map<String,Object>> list() {
        var items = service.findAll();
        String msg = items.isEmpty() ? "no hay productos" : "ok";
        return ResponseEntity.ok(Map.of("message", msg, "data", items));
    }

    @GetMapping("/{id}")
    public ProductResponseDTO get(@PathVariable Long id) { return service.findById(id); }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<Map<String,Object>> listAdmin() {
        var items = service.findAllIncludingInactive();
        String msg = items.isEmpty() ? "no hay productos" : "ok";
        return ResponseEntity.ok(Map.of("message", msg, "data", items));
}


    @PostMapping
    public ResponseEntity<ProductResponseDTO> create(@Valid @RequestBody ProductCreateDTO dto)
            throws ProductDuplicateException {
        var created = service.create(dto);
        return ResponseEntity.created(URI.create("/api/products/" + created.id())).body(created);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> createWithImages(
            @RequestParam String name, @RequestParam String description,
            @RequestParam Double price, @RequestParam Integer stock,
            @RequestParam Long categoryId, @RequestParam("files") List<MultipartFile> files)
            throws IOException, ProductDuplicateException {
        return ResponseEntity.ok(service.createWithImages(name, description, price, stock, categoryId, files));
    }

    @PatchMapping("/{id}")
    public ProductResponseDTO partialUpdate(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO dto) {
        return service.partialUpdate(id, dto);
    }

    // A) PATCH que suma stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Map<String,String>> addStock(@PathVariable Long id, @RequestBody StockPatch body) {
        service.addToStock(id, body.delta());
        return ResponseEntity.ok(Map.of("message","stock actualizado"));
    }
    public record StockPatch(int delta) {}

    // C) baja lógica con mensaje
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String,String>> bajaLogica(@PathVariable Long id) {
    service.delete(id); // ya hace baja lógica (activo=false)
    return ResponseEntity.ok(Map.of("message","prod modificado con exito"));
}


    @PatchMapping("/{id}/activar")
    public ResponseEntity<Map<String,String>> activar(@PathVariable Long id) {
        service.activar(id);
        return ResponseEntity.ok(Map.of("message","prod modificado con exito"));
    }
}
