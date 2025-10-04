package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.entity.ProductImage;
import com.uade.tpo.Marketplace.service.ProductImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/api/products/{productId}/images")
public class ProductImageController {

    @Autowired
    private ProductImageService service;

    @PostMapping
    public ResponseEntity<ProductImage> upload(@PathVariable Long productId,
                                               @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.saveImage(productId, file));
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long productId, @PathVariable Long imageId) {
        var image = service.getImage(imageId);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFileName() + "\"")
            .contentType(MediaType.parseMediaType(image.getContentType()))
            .body(image.getData());
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(@PathVariable Long imageId) {
        service.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}