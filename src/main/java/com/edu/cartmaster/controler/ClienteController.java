package com.edu.cartmaster.controler;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.service.ClienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    //Controlador GET para traer todos los clientes de la base de datos.
    @GetMapping("/lista")
    public List<Cliente> listarClientes() {
        return clienteService.obtenerTodos();
    }

    //Controlador POST para crear un cliente en la base de datos.
    @PostMapping("/agregar")
    public ResponseEntity<Cliente> crearCliente(@RequestBody Cliente cliente) {
        Cliente nuevo = clienteService.crearCliente(cliente);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    //Controlador DELETE para
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Integer id) {
        clienteService.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
