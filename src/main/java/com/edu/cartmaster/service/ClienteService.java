package com.edu.cartmaster.service;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.repository.ClienteRepository;
import com.edu.cartmaster.repository.TarjetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TarjetaRepository tarjetaRepository;

    //Servicio que usa metodo findAll del repositorio para traer todos los clientes.
    public List<Cliente> obtenerTodos() {
        return clienteRepository.findAll();
    }

    //Servicio que usa save del repositorio para guardar un cliente en la base de datos.
    public Cliente crearCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    //Servicio que elimina un cliente en la bse de datos.
    public void eliminarCliente(Integer id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar, cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Transactional
    public void eliminarClienteYTarjetas(Integer clienteId) {
        // Primero verificamos si el cliente existe
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));

        // Eliminamos primero las tarjetas asociadas al cliente
        tarjetaRepository.deleteByClienteClienteId(clienteId);

        // Finalmente eliminamos el cliente
        clienteRepository.delete(cliente);
    }

}
