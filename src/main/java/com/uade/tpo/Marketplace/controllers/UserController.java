package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.entity.Role;
import com.uade.tpo.Marketplace.entity.User;
import com.uade.tpo.Marketplace.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users") // 👈 Ojo: el prefijo es /api/users
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    // Obtener todos los usuarios
    @GetMapping
    public List<User> list() {
        return service.findAll();
    }

    // Obtener usuario por ID
    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        return service.findById(id);
    }

    // Obtener usuario por email
    @GetMapping("/by-email")
    public User byEmail(@RequestParam String email) {
        return service.findByEmail(email);
    }

    // Crear usuario
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User body) {
        User created = service.create(body);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(created);
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @RequestBody User body) {
        return service.update(id, body);
    }

    // Borrar usuario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Cambiar rol de usuario (ej: USER -> ADMIN)
    @PatchMapping("/{id}/role")
    public User changeRole(@PathVariable Long id, @RequestParam Role role) {
        return service.changeRole(id, role);
    }
}