package com.uade.tpo.Marketplace.controllers;
import com.uade.tpo.Marketplace.entity.dtos.*;
import com.uade.tpo.Marketplace.service.CompraService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
public class CompraController {
    
  @Autowired 
  private CompraService compraService;

  // USER autenticado: ver sus compras
  @GetMapping("/mias")
  public List<CompraResponseDTO> myOrders(org.springframework.security.core.Authentication auth) {
    return compraService.findMineByEmail(auth.getName()); // auth.getName() = email del token
  }

  // ADMIN: ya existentes
  @GetMapping public List<CompraResponseDTO> list(){ return compraService.findAll(); }
  @GetMapping("/{id}") public CompraResponseDTO get(@PathVariable Long id){ return compraService.findById(id); }

  // USER autenticado: crear su compra (recomendado)
  @PostMapping
  public ResponseEntity<CompraResponseDTO> create(@Valid @RequestBody CompraCreateDTO dto,
                                                  org.springframework.security.core.Authentication auth){
    var c = compraService.createForEmail(auth.getName(), dto);
    return ResponseEntity.created(URI.create("/api/compras/" + c.id())).body(c);
  }
}
