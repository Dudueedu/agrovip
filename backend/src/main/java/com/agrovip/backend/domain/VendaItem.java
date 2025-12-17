package com.agrovip.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "venda_item")
public class VendaItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer quantidade;
    
    @Column(name = "preco_unitario")
    private BigDecimal precoUnitario;
    
    @JsonIgnore 
    @ManyToOne
    @JoinColumn(name = "venda_id")
    private Venda venda;
    
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "produto_id")
    private Produto produto;
}