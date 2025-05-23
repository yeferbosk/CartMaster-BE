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

@RestController
@RequestMapping("/api/tarjetas")
@CrossOrigin(origins = "*")
public class TarjetaController {

    @Autowired
    private TarjetaService tarjetaService;

    @GetMapping
    public ResponseEntity<List<Tarjeta>> obtenerTodasLasTarjetas() {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTodasLasTarjetas();
        return ResponseEntity.ok(tarjetas);
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasPorCliente(@PathVariable Integer clienteId) {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTarjetasPorClienteId(clienteId);
        return ResponseEntity.ok(tarjetas);
    }

    @GetMapping("/{tarjetaId}")
    public ResponseEntity<Tarjeta> obtenerTarjetaPorId(@PathVariable Integer tarjetaId) {
        return tarjetaService.obtenerTarjetaPorId(tarjetaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{tarjetaId}")
    public ResponseEntity<Tarjeta> actualizarTarjeta(
            @PathVariable Integer tarjetaId,
            @RequestBody Tarjeta tarjeta) {
        return tarjetaService.actualizarTarjeta(tarjetaId, tarjeta)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{tarjetaId}")
    public ResponseEntity<Tarjeta> inactivarTarjeta(@PathVariable Integer tarjetaId) {
        return tarjetaService.inactivarTarjeta(tarjetaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/con-clientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerTarjetasConClientes() {
        List<Tarjeta> tarjetas = tarjetaService.obtenerTarjetasConClientes();
        
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

    @PostMapping("/crear/{clienteId}")
    public ResponseEntity<Tarjeta> crearTarjeta(
            @PathVariable Integer clienteId,
            @RequestBody Tarjeta tarjeta) {

        Tarjeta nueva = tarjetaService.registrarTarjeta(tarjeta, clienteId);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

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