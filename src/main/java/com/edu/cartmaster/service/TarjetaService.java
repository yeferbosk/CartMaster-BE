package com.edu.cartmaster.service;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.repository.ClienteRepository;
import com.edu.cartmaster.repository.TarjetaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TarjetaService {

    @Autowired
    private TarjetaRepository tarjetaRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    public List<Tarjeta> obtenerTodasLasTarjetas() {
        return tarjetaRepository.findAll();
    }

    public List<Tarjeta> obtenerTarjetasPorClienteId(Integer clienteId) {
        return tarjetaRepository.findByClienteClienteId(clienteId);
    }

    public Optional<Tarjeta> obtenerTarjetaPorId(Integer tarjetaId) {
        return tarjetaRepository.findById(tarjetaId);
    }

    public Optional<Tarjeta> actualizarTarjeta(Integer tarjetaId, Tarjeta tarjetaActualizada) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjetaExistente -> {
                    // Actualizamos solo los campos permitidos
                    if (tarjetaActualizada.getTarjetaEstado() != null) {
                        tarjetaExistente.setTarjetaEstado(tarjetaActualizada.getTarjetaEstado());
                    }
                    if (tarjetaActualizada.getTarjetaCupoTotal() != null) {
                        tarjetaExistente.setTarjetaCupoTotal(tarjetaActualizada.getTarjetaCupoTotal());
                    }
                    if (tarjetaActualizada.getTarjetaCupoDisponible() != null) {
                        tarjetaExistente.setTarjetaCupoDisponible(tarjetaActualizada.getTarjetaCupoDisponible());
                    }
                    // No actualizamos tarjetaCupoUtilizado ya que es un campo calculado
                    return tarjetaRepository.save(tarjetaExistente);
                });
    }

    @Transactional
    public Optional<Tarjeta> inactivarTarjeta(Integer tarjetaId) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjeta -> {
                    tarjeta.setTarjetaEstado("INACTIVO");
                    return tarjetaRepository.save(tarjeta);
                });
    }

    public List<Tarjeta> obtenerTarjetasConClientes() {
        // Utilizamos findAll() que traerá las tarjetas con sus clientes gracias a la relación @ManyToOne
        return tarjetaRepository.findAll();
    }

    public Tarjeta registrarTarjeta(Tarjeta tarjeta, Integer clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente no encontrado con ID: " + clienteId);
        }

        // Asociar cliente a la tarjeta
        tarjeta.setCliente(clienteOpt.get());

        // Guardar tarjeta (MySQL calculará tarjetaCupoUtilizado si es columna generada)
        return tarjetaRepository.save(tarjeta);
    }

    public Optional<Tarjeta> actualizarTarjetaConCupos(Integer tarjetaId, Tarjeta tarjetaActualizada) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjetaExistente -> {
                    // Actualizar estado
                    if (tarjetaActualizada.getTarjetaEstado() != null) {
                        tarjetaExistente.setTarjetaEstado(tarjetaActualizada.getTarjetaEstado());
                    }

                    // Actualizar fecha de vencimiento
                    if (tarjetaActualizada.getTarjetaFechaVencimiento() != null) {
                        tarjetaExistente.setTarjetaFechaVencimiento(tarjetaActualizada.getTarjetaFechaVencimiento());
                    }

                    // Actualizar franquicia
                    if (tarjetaActualizada.getTarjetaFranquicia() != null) {
                        tarjetaExistente.setTarjetaFranquicia(tarjetaActualizada.getTarjetaFranquicia());
                    }

                    // Actualizar cupo total
                    if (tarjetaActualizada.getTarjetaCupoTotal() != null) {
                        tarjetaExistente.setTarjetaCupoTotal(tarjetaActualizada.getTarjetaCupoTotal());
                    }

                    // Actualizar cupo disponible
                    if (tarjetaActualizada.getTarjetaCupoDisponible() != null) {
                        tarjetaExistente.setTarjetaCupoDisponible(tarjetaActualizada.getTarjetaCupoDisponible());
                    }

                    // cupoUtilizado no se actualiza directamente (es generado)
                    return tarjetaRepository.save(tarjetaExistente);
                });
    }


} 