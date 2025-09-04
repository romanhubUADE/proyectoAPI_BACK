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
    private CompraService CompraService;

    @GetMapping public List<CompraResponseDTO> list(){ return CompraService.findAll(); }

    @GetMapping("/{id}") public CompraResponseDTO get(@PathVariable Long id){ return CompraService.findById(id); }

    @PostMapping
    public ResponseEntity<CompraResponseDTO> create(@Valid @RequestBody CompraCreateDTO dto){
        var c = CompraService.create(dto);
        return ResponseEntity.created(URI.create("/api/compras/" + c.id())).body(c);
    }
}

