package com.agrovip.backend.service;

import com.agrovip.backend.domain.Cliente;
import com.agrovip.backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository repository;

    public ClienteService(ClienteRepository repository) {
        this.repository = repository;
    }

    public List<Cliente> listarTodos() {
        return repository.findAll();
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        return repository.findById(id);
    }
    
    public Cliente salvar(Cliente cliente) {
        return repository.save(cliente);
    }

    public void deletar(Integer id) {
        repository.deleteById(id);
    }
    
    public boolean existe(Integer id) {
        return repository.existsById(id);
    }

    
}