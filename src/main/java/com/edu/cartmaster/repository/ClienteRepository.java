package com.edu.cartmaster.repository;

import com.edu.cartmaster.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository  extends JpaRepository<Cliente, Integer> {
    Optional<Cliente> findById(int id);
    Optional<Cliente> findByClienteCorreo(String correo);
}
