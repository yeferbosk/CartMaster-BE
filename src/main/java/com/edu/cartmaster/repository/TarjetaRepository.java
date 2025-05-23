package com.edu.cartmaster.repository;

import com.edu.cartmaster.model.Tarjeta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

public interface TarjetaRepository extends JpaRepository<Tarjeta, Integer> {
    List<Tarjeta> findByCliente_ClienteId(Integer clienteId);

    List<Tarjeta> findByClienteClienteId(Integer clienteId);
    void deleteByClienteClienteId(Integer clienteId);
}
