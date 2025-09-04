package com.uade.tpo.Marketplace.service;

import com.uade.tpo.Marketplace.entity.*;
import com.uade.tpo.Marketplace.entity.dtos.*;
import com.uade.tpo.Marketplace.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;
import static org.springframework.http.HttpStatus.*;

@Service
@Transactional
public class CompraService {

  @Autowired private OrderRepository orderRepo;
  @Autowired private OrderProductRepository orderProductRepo; // o usá cascade desde Order
  @Autowired private UserRepository userRepo;
  @Autowired private ProductRepository productRepo;

  @Transactional(readOnly = true)
  public List<CompraResponseDTO> findAll(){
    return orderRepo.findAll().stream().map(this::toDto).toList();
  }

  @Transactional(readOnly = true)
  public CompraResponseDTO findById(Long id){
    var o = orderRepo.findById(id)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Compra no encontrada: " + id));
    return toDto(o);
  }

  public CompraResponseDTO create(CompraCreateDTO dto){
    var user = userRepo.findById(dto.userId())
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Usuario no encontrado: " + dto.userId()));

    // Cabecera (factura)
    var o = new Order();
    o.setUser(user);
    o.setDate(LocalDateTime.now());
    o.setTotal(0L);                     // si querés decimales, pasa Order.total a Double/BigDecimal
    o = orderRepo.save(o);

    double total = 0.0;

    for (var it : dto.items()){
      var p = productRepo.findById(it.productId())
          .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Producto no encontrado: " + it.productId()));

      if (it.quantity() <= 0) throw new ResponseStatusException(BAD_REQUEST, "quantity >= 1");
      if (p.getStock() < it.quantity())
        throw new ResponseStatusException(BAD_REQUEST, "stock insuficiente para productId=" + p.getId());

      // descontar stock
      p.setStock(p.getStock() - it.quantity());
      productRepo.save(p);

      // ítem (tabla order_product)
      var op = new Order_Product(o, p, it.quantity()); // asegurá constructor o setters
      orderProductRepo.save(op);

      total += p.getPrice() * it.quantity();
    }

    o.setTotal(Math.round(total));      // si cambias a Double, usa o.setTotal(total)
    o = orderRepo.save(o);

    return toDto(o);
  }

  private CompraResponseDTO toDto(Order o){
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
        o.getTotal(),              // Long hoy; cambia a Double si migrás
        o.getDate(),
        items
    );
  }
}