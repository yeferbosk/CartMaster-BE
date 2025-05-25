package com.edu.cartmaster.service;

import com.edu.cartmaster.model.Administrador;
import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.repository.AdministradorRepostory;
import com.edu.cartmaster.repository.ClienteRepository;
import com.edu.cartmaster.repository.TarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private TarjetaRepository tarjetaRepository;

    @Mock
    private AdministradorRepostory administradorRepository;

    @InjectMocks
    private ClienteService clienteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarCliente_ConDatosValidos_DebeRegistrarCliente() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setClienteCorreo("test@gmail.com");
        cliente.setClienteContrasena("password123");
        cliente.setClienteNombre("Test User");

        when(clienteRepository.findByClienteCorreo(cliente.getClienteCorreo())).thenReturn(Optional.empty());
        when(clienteRepository.save(any(Cliente.class))).thenReturn(cliente);

        // Act
        Cliente resultado = clienteService.registrarCliente(cliente);

        // Assert
        assertNotNull(resultado);
        assertEquals(cliente.getClienteCorreo(), resultado.getClienteCorreo());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void registrarCliente_ConCorreoExistente_DebeLanzarExcepcion() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setClienteCorreo("existente@gmail.com");

        when(clienteRepository.findByClienteCorreo(cliente.getClienteCorreo()))
                .thenReturn(Optional.of(new Cliente()));

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            clienteService.registrarCliente(cliente);
        });

        assertEquals("El correo ya está registrado", exception.getMessage());
    }

    @Test
    void registrarCliente_ConCorreoInvalido_DebeLanzarExcepcion() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setClienteCorreo("correo-invalido");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            clienteService.registrarCliente(cliente);
        });

        assertEquals("El formato del correo es inválido", exception.getMessage());
    }

    @Test
    void registrarCliente_ConDominioNoPermitido_DebeLanzarExcepcion() {
        // Arrange
        Cliente cliente = new Cliente();
        cliente.setClienteCorreo("test@hotmail.com");

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            clienteService.registrarCliente(cliente);
        });

        assertEquals("Solo se permiten correos con dominios @gmail.com o @correo.com", exception.getMessage());
    }

    @Test
    void login_ComoAdministrador_DebeRetornarMapaConTipoAdministrador() {
        // Arrange
        String correo = "admin@gmail.com";
        String contrasena = "admin123";
        Administrador admin = new Administrador();
        admin.setCorreo(correo);
        admin.setContrasena(contrasena);

        when(administradorRepository.findByCorreo(correo)).thenReturn(Optional.of(admin));

        // Act
        Map<String, Object> resultado = clienteService.login(correo, contrasena);

        // Assert
        assertNotNull(resultado);
        assertEquals("ADMINISTRADOR", resultado.get("tipo"));
    }

    @Test
    void login_ComoCliente_DebeRetornarMapaConTipoClienteYId() {
        // Arrange
        String correo = "cliente@gmail.com";
        String contrasena = "cliente123";
        Cliente cliente = new Cliente();
        cliente.setClienteId(1);
        cliente.setClienteCorreo(correo);
        cliente.setClienteContrasena(contrasena);

        when(clienteRepository.findByClienteCorreo(correo)).thenReturn(Optional.of(cliente));

        // Act
        Map<String, Object> resultado = clienteService.login(correo, contrasena);

        // Assert
        assertNotNull(resultado);
        assertEquals("CLIENTE", resultado.get("tipo"));
        assertEquals(1, resultado.get("clienteId"));
    }

    @Test
    void login_ConCredencialesInvalidas_DebeRetornarNull() {
        // Arrange
        String correo = "noexiste@gmail.com";
        String contrasena = "invalid";

        when(administradorRepository.findByCorreo(correo)).thenReturn(Optional.empty());
        when(clienteRepository.findByClienteCorreo(correo)).thenReturn(Optional.empty());

        // Act
        Map<String, Object> resultado = clienteService.login(correo, contrasena);

        // Assert
        assertNull(resultado);
    }

    @Test
    void obtenerTarjetasPorClienteId_DebeRetornarListaDeTarjetas() {
        // Arrange
        Integer clienteId = 1;
        List<Tarjeta> tarjetasEsperadas = Arrays.asList(new Tarjeta(), new Tarjeta());

        when(tarjetaRepository.findByCliente_ClienteId(clienteId)).thenReturn(tarjetasEsperadas);

        // Act
        List<Tarjeta> resultado = clienteService.obtenerTarjetasPorClienteId(clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(tarjetasEsperadas.size(), resultado.size());
    }

    @Test
    void eliminarCliente_ClienteExistente_DebeEliminarCliente() {
        // Arrange
        Integer clienteId = 1;
        when(clienteRepository.existsById(clienteId)).thenReturn(true);

        // Act
        clienteService.eliminarCliente(clienteId);

        // Assert
        verify(clienteRepository).deleteById(clienteId);
    }

    @Test
    void eliminarCliente_ClienteNoExistente_DebeLanzarExcepcion() {
        // Arrange
        Integer clienteId = 999;
        when(clienteRepository.existsById(clienteId)).thenReturn(false);

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            clienteService.eliminarCliente(clienteId);
        });

        assertEquals("No se puede eliminar, cliente no encontrado con ID: " + clienteId, exception.getMessage());
    }

    @Test
    void eliminarClienteYTarjetas_ClienteExistente_DebeEliminarClienteYTarjetas() {
        // Arrange
        Integer clienteId = 1;
        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);

        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));

        // Act
        clienteService.eliminarClienteYTarjetas(clienteId);

        // Assert
        verify(tarjetaRepository).deleteByClienteClienteId(clienteId);
        verify(clienteRepository).delete(cliente);
    }

    @Test
    void eliminarClienteYTarjetas_ClienteNoExistente_DebeLanzarExcepcion() {
        // Arrange
        Integer clienteId = 999;
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            clienteService.eliminarClienteYTarjetas(clienteId);
        });

        assertEquals("Cliente no encontrado con ID: " + clienteId, exception.getMessage());
    }
} 