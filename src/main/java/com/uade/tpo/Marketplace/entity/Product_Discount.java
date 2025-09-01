package com.uade.tpo.Marketplace.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity 
@Data 
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "product_discount")
public class Product_Discount {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false) private BigDecimal percentage;
  @Column(nullable = false) private LocalDateTime startDate;
  @Column(nullable = false) private LocalDateTime endDate;
}
