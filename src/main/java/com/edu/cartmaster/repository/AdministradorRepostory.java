package com.edu.cartmaster.repository;

import com.edu.cartmaster.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

    public interface AdministradorRepostory extends JpaRepository<Administrador, Integer> {

    Optional<Administrador> findByCorreo(String correo);

}
