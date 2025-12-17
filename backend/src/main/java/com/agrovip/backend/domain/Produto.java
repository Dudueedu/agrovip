package com.agrovip.backend.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "produto")
public class Produto implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nome;
    
    @Column(name = "preco_venda")
    private BigDecimal precoVenda;
    
    @Column(name = "quantidade_estoque")
    private Integer quantidadeEstoque;
}