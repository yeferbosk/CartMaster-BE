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

    public List<Tarjeta> obtenerTarjetasConClientes() {
        return tarjetaRepository.findAll();
    }

    public Tarjeta registrarTarjeta(Tarjeta tarjeta, Integer clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente no encontrado con ID: " + clienteId);
        }
        tarjeta.setCliente(clienteOpt.get());
        return tarjetaRepository.save(tarjeta);
    }

    public Optional<Tarjeta> actualizarTarjeta(Integer tarjetaId, Tarjeta tarjetaActualizada) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjetaExistente -> {
                    if (tarjetaActualizada.getTarjetaEstado() != null) {
                        tarjetaExistente.setTarjetaEstado(tarjetaActualizada.getTarjetaEstado());
                    }
                    if (tarjetaActualizada.getTarjetaCupoTotal() != null) {
                        tarjetaExistente.setTarjetaCupoTotal(tarjetaActualizada.getTarjetaCupoTotal());
                    }
                    if (tarjetaActualizada.getTarjetaCupoDisponible() != null) {
                        tarjetaExistente.setTarjetaCupoDisponible(tarjetaActualizada.getTarjetaCupoDisponible());
                    }
                    return tarjetaRepository.save(tarjetaExistente);
                });
    }

    public Optional<Tarjeta> actualizarTarjetaConCupos(Integer tarjetaId, Tarjeta tarjetaActualizada) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjetaExistente -> {
                    if (tarjetaActualizada.getTarjetaEstado() != null) {
                        tarjetaExistente.setTarjetaEstado(tarjetaActualizada.getTarjetaEstado());
                    }
                    if (tarjetaActualizada.getTarjetaFechaVencimiento() != null) {
                        tarjetaExistente.setTarjetaFechaVencimiento(tarjetaActualizada.getTarjetaFechaVencimiento());
                    }
                    if (tarjetaActualizada.getTarjetaFranquicia() != null) {
                        tarjetaExistente.setTarjetaFranquicia(tarjetaActualizada.getTarjetaFranquicia());
                    }
                    if (tarjetaActualizada.getTarjetaCupoTotal() != null) {
                        tarjetaExistente.setTarjetaCupoTotal(tarjetaActualizada.getTarjetaCupoTotal());
                    }
                    if (tarjetaActualizada.getTarjetaCupoDisponible() != null) {
                        tarjetaExistente.setTarjetaCupoDisponible(tarjetaActualizada.getTarjetaCupoDisponible());
                    }
                    return tarjetaRepository.save(tarjetaExistente);
                });
    }

    public Optional<Tarjeta> actualizarDatosGeneralesTarjeta(Integer tarjetaId, Tarjeta tarjetaActualizada) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjetaExistente -> {
                    if (tarjetaActualizada.getTarjetaNumero() != null) {
                        tarjetaExistente.setTarjetaNumero(tarjetaActualizada.getTarjetaNumero());
                    }
                    if (tarjetaActualizada.getTarjetaFechaVencimiento() != null) {
                        tarjetaExistente.setTarjetaFechaVencimiento(tarjetaActualizada.getTarjetaFechaVencimiento());
                    }
                    if (tarjetaActualizada.getTarjetaFranquicia() != null) {
                        tarjetaExistente.setTarjetaFranquicia(tarjetaActualizada.getTarjetaFranquicia());
                    }
                    if (tarjetaActualizada.getTarjetaEstado() != null) {
                        tarjetaExistente.setTarjetaEstado(tarjetaActualizada.getTarjetaEstado());
                    }
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

    public Optional<Tarjeta> actualizarCupoDisponible(Integer tarjetaId, double nuevoCupoDisponible) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjeta -> {
                    tarjeta.setTarjetaCupoDisponible(nuevoCupoDisponible);
                    return tarjetaRepository.save(tarjeta);
                });
    }

}
