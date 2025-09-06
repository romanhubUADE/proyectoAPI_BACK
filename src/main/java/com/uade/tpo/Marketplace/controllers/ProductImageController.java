package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.entity.ProductImage;
import com.uade.tpo.Marketplace.entity.dtos.ProductImageDTO;
import com.uade.tpo.Marketplace.service.ProductImageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;


@RestController
@RequestMapping("/products/{productId}/images")
public class ProductImageController {

    private ProductImageService service;

    public ProductImageController(ProductImageService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ProductImage> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(service.saveImage(productId, file));
    }

    @GetMapping
    public ResponseEntity<List<ProductImageDTO>> getImages(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getImagesByProduct(productId));
    }

    

    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long productId, @PathVariable Long imageId) {
        ProductImage image = service.getImage(imageId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + image.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(image.getContentType()))
                .body(image.getData());
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long imageId) {
        service.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}