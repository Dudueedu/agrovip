package com.agrovip.backend.controller;

import com.agrovip.backend.domain.Produto;
import com.agrovip.backend.service.ProdutoService; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<Produto> getAll() {
        return service.listarTodos(); 
    }

    @PostMapping
    public Produto create(@RequestBody Produto produto) {
        return service.salvar(produto); 
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Produto> getById(@PathVariable Integer id) {
        return service.buscarPorId(id) 
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> update(@PathVariable Integer id, @RequestBody Produto produto) {
        if (!service.existe(id)) { 
            return ResponseEntity.notFound().build();
        }
        produto.setId(id);
        return ResponseEntity.ok(service.salvar(produto)); 
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