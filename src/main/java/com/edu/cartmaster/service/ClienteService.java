package com.edu.cartmaster.service;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.repository.AdministradorRepostory;
import com.edu.cartmaster.repository.ClienteRepository;
import com.edu.cartmaster.repository.TarjetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TarjetaRepository tarjetaRepository;
    private final AdministradorRepostory administradorRepository;
  
    //Metodo para registrar un cliente
    public Cliente registrarCliente(Cliente cliente) {
        // Verifica si el correo ya existe
        boolean yaExiste = clienteRepository.findByClienteCorreo(cliente.getClienteCorreo()).isPresent();
        if (yaExiste) {
            throw new RuntimeException("El correo ya está registrado");
        }
          return clienteRepository.save(cliente);
    }

    //Metodo para logear un cliente
    public String login(String correo, String contrasena) {
        // Verificar si es administrador
        Optional<com.edu.cartmaster.model.Administrador> adminOpt = administradorRepository.findByCorreo(correo);
        if (adminOpt.isPresent()) {
            if (adminOpt.get().getContrasena().equals(contrasena)) {
                return "ADMINISTRADOR";
            }
        }

        // Verificar si es cliente
        Optional<Cliente> clienteOpt = clienteRepository.findByClienteCorreo(correo);
        if (clienteOpt.isPresent()) {
            if (clienteOpt.get().getClienteContrasena().equals(contrasena)) {
                return "CLIENTE";
            }
        }

        return "CREDENCIALES_INVALIDAS";
    }

    public List<Tarjeta> obtenerTarjetasPorClienteId(Integer clienteId) {
        return tarjetaRepository.findByCliente_ClienteId(clienteId);
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
