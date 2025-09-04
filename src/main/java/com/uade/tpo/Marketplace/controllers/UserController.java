package com.uade.tpo.Marketplace.controllers;

import com.uade.tpo.Marketplace.entity.Role;

import com.uade.tpo.Marketplace.service.UserService;
import com.uade.tpo.Marketplace.entity.dtos.UserCreateDTO;
import com.uade.tpo.Marketplace.entity.dtos.UserUpdateDTO;
import com.uade.tpo.Marketplace.entity.dtos.UserResponseDTO;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
  @Autowired private UserService service;

  @GetMapping
  public List<UserResponseDTO> list() { return service.findAll(); }

  @GetMapping("/{id}")
  public UserResponseDTO get(@PathVariable Long id) { return service.findById(id); }

  @GetMapping("/by-email")
  public UserResponseDTO byEmail(@RequestParam String email) { return service.findByEmail(email); }

  @PostMapping
  public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserCreateDTO body) {
    var created = service.create(body);
    return ResponseEntity.created(URI.create("/api/users/" + created.id())).body(created);
  }

  @PutMapping("/{id}")
  public UserResponseDTO update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO body) {
    return service.update(id, body);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    service.delete(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/role")
  public UserResponseDTO changeRole(@PathVariable Long id, @RequestParam Role role) {
    return service.changeRole(id, role);
  }
}