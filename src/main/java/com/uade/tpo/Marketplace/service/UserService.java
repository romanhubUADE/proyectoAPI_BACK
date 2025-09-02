package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Role;
import com.uade.tpo.Marketplace.entity.User;
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


    @Transactional(readOnly = true)
    public List<User> findAll() { return repo.findAll(); }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        return repo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found by email: " + email));
    }

    public User create(User u) {
        if (u.getId() != null) u.setId(null);
        if (u.getRole() == null) u.setRole(Role.USER);
        if (repo.existsByEmail(u.getEmail())) throw new IllegalArgumentException("Email already exists");
        return repo.save(u);
    }

    public User update(Long id, User u) {
        User db = findById(id);
        if (!db.getEmail().equals(u.getEmail()) && repo.existsByEmail(u.getEmail()))
            throw new IllegalArgumentException("Email already exists");
        db.setFirstName(u.getFirstName());
        db.setLastName(u.getLastName());
        db.setEmail(u.getEmail());
        db.setPassword(u.getPassword());
        db.setRole(u.getRole() != null ? u.getRole() : db.getRole());
        return repo.save(db);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("User not found: " + id);
        repo.deleteById(id);
    }

    public User changeRole(Long id, Role role) {
        User db = findById(id);
        db.setRole(role);
        return repo.save(db);
    }
}