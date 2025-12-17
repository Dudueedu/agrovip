package com.agrovip.backend.controller;

import com.agrovip.backend.domain.Venda;
import com.agrovip.backend.service.VendaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
public class VendaController {

    private final VendaService service;

    public VendaController(VendaService service) {
        this.service = service;
    }

    @GetMapping
    public List<Venda> getAll() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> getById(@PathVariable Integer id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cliente/{clienteId}")
    public Venda create(@RequestBody Venda venda, @PathVariable Integer clienteId) {
        return service.salvar(venda, clienteId);
    }

    @PutMapping("/{id}/cliente/{clienteId}")
    public ResponseEntity<Venda> update(@PathVariable Integer id, @RequestBody Venda venda, @PathVariable Integer clienteId) {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        venda.setId(id); 
        return ResponseEntity.ok(service.salvar(venda, clienteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}