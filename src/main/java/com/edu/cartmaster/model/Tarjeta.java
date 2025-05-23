package com.edu.cartmaster.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tarjeta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tarjetaId;

    @Column(nullable = false, unique = true, length = 16)
    private String tarjetaNumero;

    @Column(nullable = false, length = 7)
    private String tarjetaFechaVencimiento;

    @Column(nullable = false)
    private String tarjetaFranquicia;

    @Column(nullable = false)
    private String tarjetaEstado = "ACTIVO";

    @Column(nullable = false)
    private Double tarjetaCupoTotal;

    @Column(nullable = false)
    private Double tarjetaCupoDisponible;

    @Column(nullable = false)
    private Double tarjetaCupoUtilizado;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

}
