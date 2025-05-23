package com.edu.cartmaster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "administrador")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Administrador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "administrador_id")
    private Integer id;

    @Column(name = "administrador_correo", nullable = false, unique = true)
    private String correo;

    @Column(name = "administrador_contrasena", nullable = false)
    private String contrasena;
}
