package com.agrovip.backend.repository;
import com.agrovip.backend.domain.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VendaRepository extends JpaRepository<Venda, Integer> {
}