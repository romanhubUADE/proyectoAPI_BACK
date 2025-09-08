package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.entity.ProductImage;
import com.uade.tpo.Marketplace.repository.ProductImageRepository;
import com.uade.tpo.Marketplace.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
public class ProductImageService {


    @Autowired
    private ProductImageRepository imageRepo;
    @Autowired
    private ProductRepository productRepo;

    public ProductImage saveImage(Long productId, MultipartFile file) throws IOException {
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        ProductImage image = new ProductImage();
        image.setFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setData(file.getBytes());
        image.setProduct(product);
        return imageRepo.save(image);
    }

    public ProductImage getImage(Long imageId) {
        return imageRepo.findById(imageId)
            .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada: " + imageId));
    }

    public void deleteImage(Long imageId) {
        imageRepo.deleteById(imageId);
    }
}