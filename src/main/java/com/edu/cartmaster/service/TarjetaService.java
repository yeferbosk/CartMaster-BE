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

/**
 * Servicio que contiene la lógica de negocio para la gestión de tarjetas.
 * Encapsula las operaciones CRUD y gestiona las asociaciones entre tarjetas y clientes.
 */
@Service
public class TarjetaService {

    @Autowired
    private TarjetaRepository tarjetaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Recupera todas las tarjetas almacenadas en el sistema.
     *
     * @return lista de todas las tarjetas.
     */
    public List<Tarjeta> obtenerTodasLasTarjetas() {
        return tarjetaRepository.findAll();
    }

    /**
     * Obtiene todas las tarjetas asociadas a un cliente específico.
     *
     * @param clienteId ID del cliente.
     * @return lista de tarjetas pertenecientes al cliente.
     */
    public List<Tarjeta> obtenerTarjetasPorClienteId(Integer clienteId) {
        return tarjetaRepository.findByClienteClienteId(clienteId);
    }

    /**
     * Obtiene una tarjeta por su identificador único.
     *
     * @param tarjetaId ID de la tarjeta.
     * @return un {@link Optional} conteniendo la tarjeta si existe.
     */
    public Optional<Tarjeta> obtenerTarjetaPorId(Integer tarjetaId) {
        return tarjetaRepository.findById(tarjetaId);
    }

    /**
     * Actualiza una tarjeta existente con los nuevos datos provistos.
     * Solo se actualizan los campos permitidos (estado, cupo total y cupo disponible).
     *
     * @param tarjetaId ID de la tarjeta a actualizar.
     * @param tarjetaActualizada objeto {@link Tarjeta} con los nuevos datos.
     * @return un {@link Optional} con la tarjeta actualizada si existe.
     */
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
                    // cupoUtilizado no se actualiza manualmente
                    return tarjetaRepository.save(tarjetaExistente);
                });
    }

    /**
     * Inactiva una tarjeta cambiando su estado a "INACTIVO".
     * Esta operación es transaccional.
     *
     * @param tarjetaId ID de la tarjeta a inactivar.
     * @return un {@link Optional} con la tarjeta inactivada si existe.
     */
    @Transactional
    public Optional<Tarjeta> inactivarTarjeta(Integer tarjetaId) {
        return tarjetaRepository.findById(tarjetaId)
                .map(tarjeta -> {
                    tarjeta.setTarjetaEstado("INACTIVO");
                    return tarjetaRepository.save(tarjeta);
                });
    }

    /**
     * Obtiene todas las tarjetas con sus clientes asociados.
     * La relación se carga automáticamente por JPA mediante la anotación {@code @ManyToOne}.
     *
     * @return lista de tarjetas con información de clientes.
     */
    public List<Tarjeta> obtenerTarjetasConClientes() {
        return tarjetaRepository.findAll();
    }

    /**
     * Registra una nueva tarjeta asociándola a un cliente existente.
     *
     * @param tarjeta objeto {@link Tarjeta} con la información a registrar.
     * @param clienteId ID del cliente al que se asociará la tarjeta.
     * @return la tarjeta creada.
     * @throws RuntimeException si el cliente no existe.
     */
    public Tarjeta registrarTarjeta(Tarjeta tarjeta, Integer clienteId) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) {
            throw new RuntimeException("Cliente no encontrado con ID: " + clienteId);
        }

        tarjeta.setCliente(clienteOpt.get());
        return tarjetaRepository.save(tarjeta);
    }

    /**
     * Actualiza una tarjeta con campos adicionales como franquicia y fecha de vencimiento.
     * Los campos actualizados son: estado, franquicia, fecha de vencimiento, cupo total y disponible.
     *
     * @param tarjetaId ID de la tarjeta a actualizar.
     * @param tarjetaActualizada objeto {@link Tarjeta} con los datos a modificar.
     * @return un {@link Optional} con la tarjeta modificada si existe.
     */
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

}
