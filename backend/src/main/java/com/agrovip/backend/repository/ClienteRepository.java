package com.agrovip.backend.repository;
import com.agrovip.backend.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
}