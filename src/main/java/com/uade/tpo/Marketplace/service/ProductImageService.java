package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Product;
import com.uade.tpo.Marketplace.entity.ProductImage;
import com.uade.tpo.Marketplace.entity.dtos.ProductImageDTO;
import com.uade.tpo.Marketplace.repository.ProductImageRepository;
import com.uade.tpo.Marketplace.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductImageService {

    private final ProductImageRepository imageRepo;
    private final ProductRepository productRepo;

    public ProductImageService(ProductImageRepository imageRepo, ProductRepository productRepo) {
        this.imageRepo = imageRepo;
        this.productRepo = productRepo;
    }

    public ProductImage saveImage(Long productId, MultipartFile file) throws IOException {
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        ProductImage image = new ProductImage();
        image.setFileName(file.getOriginalFilename());
        image.setContentType(file.getContentType());
        image.setData(file.getBytes());
        image.setProduct(product);

        return imageRepo.save(image);
    }

    public List<ProductImageDTO> getImagesByProduct(Long productId) {
        return imageRepo.findByProductId(productId).stream()
                .map(img -> new ProductImageDTO(
                        img.getId(),
                        img.getFileName(),
                        img.getContentType(),
                        Base64.getEncoder().encodeToString(img.getData())
                ))
                .collect(Collectors.toList());
    }

    public ProductImage getImage(Long id) {
        return imageRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Imagen no encontrada: " + id));
    }

    public void deleteImage(Long id) {
        imageRepo.deleteById(id);
    }
}
