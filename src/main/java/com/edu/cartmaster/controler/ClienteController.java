package com.edu.cartmaster.controler;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    //EndPoint de registro
    @PostMapping("/registro")
    public ResponseEntity<?> registrarCliente(@RequestBody Cliente cliente) {
        try {
            Cliente nuevo = clienteService.registrarCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
    //EndPont de login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        String correo = credenciales.get("correo");
        String contrasena = credenciales.get("contrasena");

        Map<String, Object> resultado = clienteService.login(correo, contrasena);

        if (resultado == null) {
            // Credenciales inválidas
            Map<String, String> error = new HashMap<>();
            error.put("error", "CREDENCIALES_INVALIDAS");
            return ResponseEntity.status(401).body(error);
        }

        // Credenciales válidas (cliente o administrador)
        return ResponseEntity.ok(resultado);
    }

    //EndPoint que trae todas las targetas de un usuario
    @GetMapping("/{id}/tarjetas")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetas(@PathVariable Integer id) {
        List<Tarjeta> tarjetas = clienteService.obtenerTarjetasPorClienteId(id);
        return ResponseEntity.ok(tarjetas);
    }

    //Controlador DELETE para eliminar un cliente y sus tarjetas.
    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Integer clienteId) {
        try {
            clienteService.eliminarClienteYTarjetas(clienteId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
