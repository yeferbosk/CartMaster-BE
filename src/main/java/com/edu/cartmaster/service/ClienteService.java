package com.edu.cartmaster.service;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.repository.ClienteRepository;
import com.edu.cartmaster.repository.TarjetaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final TarjetaRepository tarjetaRepository;

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
        // Buscar cliente
        Optional<Cliente> clienteOpt = clienteRepository.findByClienteCorreo(correo);
        if (clienteOpt.isPresent() && clienteOpt.get().getClienteContrasena().equals(contrasena)) {
            return "CLIENTE";
        }
        return "CREDENCIALES_INVALIDAS";
    }

    public List<Tarjeta> obtenerTarjetasPorClienteId(Integer clienteId) {
        return tarjetaRepository.findByCliente_ClienteId(clienteId);
    }


}
