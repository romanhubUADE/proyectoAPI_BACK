package com.uade.tpo.Marketplace.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Double price;
    private int stock;
    private String description;
    @ManyToOne @JoinColumn(name="category_id", nullable=false)
    private Category category;

    //Campo para Baja logica de Producto
    @Column(nullable = false)
    private Boolean activo = true;


}
