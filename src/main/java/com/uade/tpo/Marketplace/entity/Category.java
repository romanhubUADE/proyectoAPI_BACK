package com.uade.tpo.Marketplace.entity;

import jakarta.persistence.*;
import lombok.*;

@Data 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class Category {
    @Id

    private Integer id;

    @Column(nullable = false)
    private String description;
}