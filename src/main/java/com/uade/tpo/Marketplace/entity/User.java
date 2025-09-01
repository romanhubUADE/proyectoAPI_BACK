package com.uade.tpo.Marketplace.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String email;
    private String password;

    @Column
    private String lastName;

    @OneToMany(mappedBy = "user")
    @JsonIgnore   // 🚀 Evita la serialización infinita
    private List<Order> orders;

    @Enumerated(EnumType.STRING)
    private Role role;
}

