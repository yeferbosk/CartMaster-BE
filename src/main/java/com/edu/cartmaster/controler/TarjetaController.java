package com.edu.cartmaster.controler;

import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.service.TarjetaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controlador REST para la gestión de tarjetas.
 * Maneja operaciones CRUD y consultas relacionadas con entidades de tipo {@link Tarjeta}.
 */
@RestController
@RequestMapping("/api/tarjetas")
@CrossOrigin(origins = "*")
public class TarjetaController {

    /**
     * Servicio encargado de la lógica de negocio relacionada con las tarjetas.
     */
    @Autowired
    private TarjetaService tarjetaService;

    /**
     * Obtiene todas las tarjetas registradas en el sistema.
     *
     * @return una respuesta HTTP con la lista de todas las tarjetas y código 200 (OK).
     */
    @GetMapping
    public ResponseEntity<List<Tarjeta>> obtenerTodasLasTarjetas() {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTodasLasTarjetas();
        return ResponseEntity.ok(tarjetas);
    }

    /**
     * Obtiene todas las tarjetas asociadas a un cliente específico.
     *
     * @param clienteId ID del cliente del cual se desean consultar las tarjetas.
     * @return una respuesta HTTP con la lista de tarjetas del cliente y código 200 (OK).
     */
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasPorCliente(@PathVariable Integer clienteId) {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTarjetasPorClienteId(clienteId);
        return ResponseEntity.ok(tarjetas);
    }

    /**
     * Obtiene una tarjeta específica por su ID.
     *
     * @param tarjetaId ID de la tarjeta a consultar.
     * @return una respuesta HTTP con la tarjeta encontrada o 404 (Not Found) si no existe.
     */
    @GetMapping("/{tarjetaId}")
    public ResponseEntity<Tarjeta> obtenerTarjetaPorId(@PathVariable Integer tarjetaId) {
        return tarjetaService.obtenerTarjetaPorId(tarjetaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Actualiza la información de una tarjeta existente.
     *
     * @param tarjetaId ID de la tarjeta que se desea actualizar.
     * @param tarjeta   Objeto {@link Tarjeta} con los nuevos datos.
     * @return una respuesta HTTP con la tarjeta actualizada o 404 (Not Found) si no existe.
     */
    @PutMapping("/{tarjetaId}")
    public ResponseEntity<Tarjeta> actualizarTarjeta(
            @PathVariable Integer tarjetaId,
            @RequestBody Tarjeta tarjeta) {
        return tarjetaService.actualizarTarjeta(tarjetaId, tarjeta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Inactiva (no elimina físicamente) una tarjeta por su ID.
     *
     * @param tarjetaId ID de la tarjeta a inactivar.
     * @return una respuesta HTTP con la tarjeta inactivada o 404 (Not Found) si no existe.
     */
    @DeleteMapping("/{tarjetaId}")
    public ResponseEntity<Tarjeta> inactivarTarjeta(@PathVariable Integer tarjetaId) {
        return tarjetaService.inactivarTarjeta(tarjetaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene una lista de tarjetas incluyendo información básica del cliente asociado.
     *
     * @return una respuesta HTTP con la lista de tarjetas enriquecidas con datos del cliente.
     */
    @GetMapping("/con-clientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerTarjetasConClientes() {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTarjetasConClientes();

        // Transforma cada tarjeta a un mapa de atributos incluyendo datos del cliente
        List<Map<String, Object>> resultado = tarjetas.stream()
                .map(tarjeta -> Map.of(
                        "tarjetaId", tarjeta.getTarjetaId(),
                        "numeroTarjeta", tarjeta.getTarjetaNumero(),
                        "fechaVencimiento", tarjeta.getTarjetaFechaVencimiento(),
                        "franquicia", tarjeta.getTarjetaFranquicia(),
                        "estado", tarjeta.getTarjetaEstado(),
                        "cupoTotal", tarjeta.getTarjetaCupoTotal(),
                        "cupoDisponible", tarjeta.getTarjetaCupoDisponible(),
                        "cupoUtilizado", tarjeta.getTarjetaCupoUtilizado(),
                        "cliente", Map.of(
                                "id", tarjeta.getCliente().getClienteId(),
                                "nombre", tarjeta.getCliente().getClienteNombre(),
                                "correo", tarjeta.getCliente().getClienteCorreo()
                        )
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultado);
    }

    /**
     * Crea una nueva tarjeta y la asocia a un cliente existente.
     *
     * @param clienteId ID del cliente al que se asignará la tarjeta.
     * @param tarjeta   Objeto {@link Tarjeta} con la información de la nueva tarjeta.
     * @return una respuesta HTTP con la tarjeta creada y código 201 (Created).
     */
    @PostMapping("/crear/{clienteId}")
    public ResponseEntity<Tarjeta> crearTarjeta(
            @PathVariable Integer clienteId,
            @RequestBody Tarjeta tarjeta) {

        Tarjeta nueva = tarjetaService.registrarTarjeta(tarjeta, clienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    /**
     * Actualiza los datos de una tarjeta, incluyendo valores relacionados con cupo.
     *
     * @param tarjetaId         ID de la tarjeta a actualizar.
     * @param tarjetaActualizada Objeto {@link Tarjeta} con los nuevos datos incluyendo cupos.
     * @return una respuesta HTTP con la tarjeta actualizada o un mensaje de error si no se encuentra.
     */
    @PutMapping("/actualizar_con_cupos/{tarjetaId}")
    public ResponseEntity<?> actualizarTarjetaConCupos(
            @PathVariable Integer tarjetaId,
            @RequestBody Tarjeta tarjetaActualizada) {

        Optional<Tarjeta> actualizada = tarjetaService.actualizarTarjeta(tarjetaId, tarjetaActualizada);

        if (actualizada.isPresent()) {
            return ResponseEntity.ok(actualizada.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Tarjeta no encontrada con ID: " + tarjetaId);
        }
    }

}
