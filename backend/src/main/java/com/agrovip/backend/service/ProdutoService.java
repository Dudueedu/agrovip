package com.agrovip.backend.service;

import com.agrovip.backend.domain.Produto;
import com.agrovip.backend.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Optional<Produto> buscarPorId(Integer id) {
        return repository.findById(id);
    }

    public Produto salvar(Produto produto) {
        return repository.save(produto);
    }

    public void deletar(Integer id) {
        repository.deleteById(id);
    }
    
    public boolean existe(Integer id) {
        return repository.existsById(id);
    }
}