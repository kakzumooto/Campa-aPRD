package com.campana.said_urban_backend.repository;

import com.campana.said_urban_backend.model.Afiliado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AfiliadoRepository extends JpaRepository<Afiliado, Long> {
}