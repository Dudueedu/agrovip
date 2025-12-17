package com.agrovip.backend.repository;

import com.agrovip.backend.domain.VendaItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaItemRepository extends JpaRepository<VendaItem, Integer> {
}