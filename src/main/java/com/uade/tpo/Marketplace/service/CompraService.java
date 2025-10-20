package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.Order;
import com.uade.tpo.Marketplace.entity.Order_Product;
import com.uade.tpo.Marketplace.entity.dtos.*;
import com.uade.tpo.Marketplace.repository.OrderProductRepository;
import com.uade.tpo.Marketplace.repository.OrderRepository;
import com.uade.tpo.Marketplace.repository.ProductRepository;
import com.uade.tpo.Marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
@Transactional
public class CompraService {

  @Autowired private OrderRepository orderRepo;
  @Autowired private OrderProductRepository orderProductRepo;
  @Autowired private UserRepository userRepo;
  @Autowired private ProductRepository productRepo;

  // ---------- Lecturas ----------
  @Transactional(readOnly = true)
  public List<CompraResponseDTO> findAll() {
    return orderRepo.findAll().stream().map(this::toDto).toList();
  }

  @Transactional(readOnly = true)
  public CompraResponseDTO findById(Long id) {
    var o = orderRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Compra no encontrada: " + id));
    return toDto(o);
  }

  // Mías por email (del token)
  @Transactional(readOnly = true)
  public List<CompraResponseDTO> findMineByEmail(String email) {
    return orderRepo.findAllByUserEmail(email).stream().map(this::toDto).toList();
  }

  // ADMIN: todas por userId
  @Transactional(readOnly = true)
  public List<CompraResponseDTO> findByUserId(Long userId) {
    return orderRepo.findAllByUser_Id(userId).stream().map(this::toDto).toList();
  }

  // ---------- Escrituras ----------

  // USUARIO AUTENTICADO: crea la compra usando el email del token
  public CompraResponseDTO createForEmail(String email, CompraCreateDTO dto) {
    var user = userRepo.findByEmail(email)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + email));
    return createInternal(user.getId(), dto);
  }

  // ADMIN: crear compra para un userId explícito (opcional)
  public CompraResponseDTO createForUserId(Long userId, CompraCreateDTO dto) {
    // valida existencia del usuario
    userRepo.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + userId));
    return createInternal(userId, dto);
  }

  // Lógica común de creación
  private CompraResponseDTO createInternal(Long userId, CompraCreateDTO dto) {
    var user = userRepo.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + userId));

    var o = new Order();
    o.setUser(user);
    o.setDate(LocalDateTime.now());
    o.setTotal(0L);
    o = orderRepo.save(o);

    double total = 0.0;
    int totalQuantity = 0;

    for (var it : dto.items()) {
      var p = productRepo.findById(it.productId())
          .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + it.productId()));

      if (it.quantity() <= 0) throw new ResponseStatusException(BAD_REQUEST, "quantity >= 1");
      if (p.getStock() < it.quantity())
        throw new ResponseStatusException(BAD_REQUEST, "stock insuficiente para productId=" + p.getId());

      p.setStock(p.getStock() - it.quantity());
      productRepo.save(p);

      var op = new Order_Product(o, p, it.quantity());
      orderProductRepo.save(op);

      total += p.getPrice() * it.quantity();
      totalQuantity += it.quantity();
    }

    // promo por cantidad (si aplica)
    if (totalQuantity >= 3) total *= 0.85;

    o.setTotal(Math.round(total));
    o = orderRepo.save(o);

    return toDto(o);
  }

  // ---------- Mapping ----------
  private CompraResponseDTO toDto(Order o) {
    var items = o.getItems().stream().map(op ->
        new CompraItemDTO(
            op.getProduct().getId(),
            op.getProduct().getName(),
            op.getQuantity(),
            op.getProduct().getPrice(),
            op.getProduct().getPrice() * op.getQuantity()
        )
    ).toList();

    return new CompraResponseDTO(
        o.getId(),
        o.getUser().getId(),
        o.getTotal(),
        o.getDate(),
        items
    );
  }
}
