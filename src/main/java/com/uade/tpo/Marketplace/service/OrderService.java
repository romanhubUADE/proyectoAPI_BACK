package com.uade.tpo.Marketplace.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.tpo.Marketplace.entity.Order;
import com.uade.tpo.Marketplace.entity.User;
import com.uade.tpo.Marketplace.entity.dtos.OrderItemDTO;
import com.uade.tpo.Marketplace.entity.dtos.OrderResponseDTO;
import com.uade.tpo.Marketplace.repository.OrderRepository;
import com.uade.tpo.Marketplace.repository.UserRepository;

@Service
@Transactional
public class OrderService {

  @Autowired private OrderRepository repo;
  @Autowired private UserRepository userRepo;

  // ---------- CRUD ENTIDADES ----------

  @Transactional(readOnly = true)
  public List<Order> findAll() { return repo.findAll(); }

  @Transactional(readOnly = true)
  public Order findById(Long id) {
    return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
  }

  public Order create(Order o) {
    if (o.getUser() == null || o.getUser().getId() == null) throw new IllegalArgumentException("User id is required");
    User user = userRepo.findById(o.getUser().getId())
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + o.getUser().getId()));
    o.setUser(user);
    o.setId(null);
    return repo.save(o);
  }

  public Order update(Long id, Order o) {
    Order db = findById(id);
    if (o.getTotal() != null) db.setTotal(o.getTotal());
    if (o.getDate() != null) db.setDate(o.getDate());
    if (o.getUser() != null && o.getUser().getId() != null) {
      User user = userRepo.findById(o.getUser().getId())
          .orElseThrow(() -> new IllegalArgumentException("User not found: " + o.getUser().getId()));
      db.setUser(user);
    }
    return repo.save(db);
  }

  public void delete(Long id) {
    if (!repo.existsById(id)) throw new IllegalArgumentException("Order not found: " + id);
    repo.deleteById(id);
  }

  // ---------- DTOs ----------

  @Transactional(readOnly = true)
  public List<OrderResponseDTO> findAllDto() {
    return repo.findAll().stream().map(this::toDto).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public OrderResponseDTO findDtoById(Long id) {
    var o = repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    return toDto(o);
  }

  // ADMIN: todas por userId
  @Transactional(readOnly = true)
  public List<OrderResponseDTO> findDtosByUserId(Long userId) {
    return repo.findAllByUser_Id(userId).stream().map(this::toDto).collect(Collectors.toList());
  }

  // USER autenticado: “mías” usando email del token (Authentication#getName)
  @Transactional(readOnly = true)
  public List<OrderResponseDTO> findDtosMineByEmail(String email) {
    return repo.findAllByUserEmail(email).stream().map(this::toDto).collect(Collectors.toList());
  }

  private OrderResponseDTO toDto(Order o) {
    var items = o.getItems().stream()
        .map(i -> new OrderItemDTO(
            i.getProduct().getId(),
            i.getProduct().getName(),
            i.getQuantity(),
            i.getProduct().getPrice(),
            i.getQuantity() * i.getProduct().getPrice()
        ))
        .collect(Collectors.toList());

    Double total = (o.getTotal() == null) ? 0d : o.getTotal().doubleValue();

    return new OrderResponseDTO(
        o.getId(),
        (o.getUser() != null ? o.getUser().getId() : null),
        o.getDate(),
        total,
        items
    );
  }
}