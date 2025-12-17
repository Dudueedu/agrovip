package com.agrovip.backend.service;

import com.agrovip.backend.domain.Cliente;
import com.agrovip.backend.domain.Produto;
import com.agrovip.backend.domain.Venda;
import com.agrovip.backend.domain.VendaItem; 
import com.agrovip.backend.repository.ClienteRepository;
import com.agrovip.backend.repository.ProdutoRepository;
import com.agrovip.backend.repository.VendaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime; 
import java.util.List;
import java.util.Optional;

@Service
public class VendaService {

    private final VendaRepository repository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public VendaService(VendaRepository repository, 
                        ClienteRepository clienteRepository, 
                        ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Venda salvar(Venda venda, Integer clienteId) {
        
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente com ID " + clienteId + " não encontrado"));
        venda.setCliente(cliente);
        
        venda.setDataVenda(LocalDateTime.now()); 

        if (venda.getItens() == null || venda.getItens().isEmpty()) {
            throw new RuntimeException("A venda deve ter pelo menos um item.");
        }

        for (VendaItem item : venda.getItens()) {
            Produto produto = produtoRepository.findById(item.getProduto().getId())
                    .orElseThrow(() -> new RuntimeException("Produto com ID " + item.getProduto().getId() + " não encontrado"));

            if (produto.getQuantidadeEstoque() < item.getQuantidade()) {
                throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
            }
            
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - item.getQuantidade());
            produtoRepository.save(produto);

            item.setVenda(venda);
        }

        return repository.save(venda);
    }

    public List<Venda> listarTodas() {
        return repository.findAll();
    }
    public Optional<Venda> buscarPorId(Integer id) {
        return repository.findById(id);
    }
    public void deletar(Integer id) {
        repository.deleteById(id);
    }
    public boolean existe(Integer id) {
        return repository.existsById(id);
    }
}