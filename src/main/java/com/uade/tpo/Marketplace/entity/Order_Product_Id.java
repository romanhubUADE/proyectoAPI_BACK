package com.uade.tpo.Marketplace.entity;

import lombok.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order_Product_Id implements Serializable {
    private Long order;
    private Long product;
}