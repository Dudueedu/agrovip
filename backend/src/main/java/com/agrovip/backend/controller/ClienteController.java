package com.agrovip.backend.controller;

import com.agrovip.backend.domain.Cliente;
import com.agrovip.backend.service.ClienteService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<Cliente> getAll() {
        return service.listarTodos();
    }

    @PostMapping
    public Cliente create(@RequestBody Cliente cliente) {
        return service.salvar(cliente);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> update(@PathVariable Integer id, @RequestBody Cliente cliente) {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build();
        }
        
        cliente.setId(id);
        
        return ResponseEntity.ok(service.salvar(cliente));
    }
}