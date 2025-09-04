package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Role;
import com.uade.tpo.Marketplace.entity.User;
import com.uade.tpo.Marketplace.entity.dtos.UserCreateDTO;
import com.uade.tpo.Marketplace.entity.dtos.UserUpdateDTO;
import com.uade.tpo.Marketplace.entity.dtos.UserResponseDTO;
import com.uade.tpo.Marketplace.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService {
    @Autowired
    private  UserRepository repo;

    private UserResponseDTO toDto(User u) {
    return new UserResponseDTO(
        u.getId(),
        u.getFirstName(),
        u.getLastName(),
        u.getEmail()
    );
}
     @Transactional(readOnly = true)
    public List<UserResponseDTO> findAll() {
        return repo.findAll().stream().map(this::toDto).toList();
  }

    @Transactional(readOnly = true)
  public UserResponseDTO findById(Long id) {
    var u = repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    return toDto(u);
  }

  @Transactional(readOnly = true)
  public UserResponseDTO findByEmail(String email) {
    var u = repo.findByEmail(email)
        .orElseThrow(() -> new IllegalArgumentException("El usuario no se encontro con el mail: " + email));
    return toDto(u);
  }

  public UserResponseDTO create(UserCreateDTO dto) {
    if (repo.existsByEmail(dto.email()))
      throw new IllegalArgumentException("El email ya esta registrado");

    var u = new User();
    u.setId(null);
    u.setFirstName(dto.firstName());
    u.setLastName(dto.lastName());
    u.setEmail(dto.email());
    u.setPassword(dto.password());        
    u.setRole(Role.USER);                 

    return toDto(repo.save(u));
  }

  public UserResponseDTO update(Long id, UserUpdateDTO dto) {
    var db = repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));

    if (dto.email() != null && !dto.email().equals(db.getEmail()) && repo.existsByEmail(dto.email()))
      throw new IllegalArgumentException("El email ya esta registrado");

    if (dto.firstName() != null) db.setFirstName(dto.firstName());
    if (dto.lastName()  != null) db.setLastName(dto.lastName());
    if (dto.email()     != null) db.setEmail(dto.email());
    if (dto.password()  != null) db.setPassword(dto.password());  // se puede actualizar
    return toDto(repo.save(db));
  }

  public void delete(Long id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("User not found: " + id);
    repo.deleteById(id);
  }

  public UserResponseDTO changeRole(Long id, Role role) {
    var db = repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    db.setRole(role);
    return toDto(repo.save(db));
  }

}