package com.edu.cartmaster.repository;

import com.edu.cartmaster.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TarjetaRepository extends JpaRepository<Tarjeta, Integer> {
    List<Tarjeta> findByClienteClienteId(Integer clienteId);
    void deleteByClienteClienteId(Integer clienteId);
} 