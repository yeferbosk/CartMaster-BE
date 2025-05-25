package com.edu.cartmaster.service;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.repository.ClienteRepository;
import com.edu.cartmaster.repository.TarjetaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TarjetaServiceTest {

    @Mock
    private TarjetaRepository tarjetaRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private TarjetaService tarjetaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registrarTarjeta_ConClienteExistente_DebeRegistrarTarjeta() {
        // Arrange
        Integer clienteId = 1;
        Cliente cliente = new Cliente();
        cliente.setClienteId(clienteId);
        
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setTarjetaNumero("4111111111111111");
        tarjeta.setTarjetaEstado("ACTIVO");
        tarjeta.setTarjetaCupoTotal(1000.0);
        tarjeta.setTarjetaCupoDisponible(1000.0);

        Tarjeta tarjetaGuardada = new Tarjeta();
        tarjetaGuardada.setTarjetaId(1);
        tarjetaGuardada.setTarjetaNumero("4111111111111111");
        tarjetaGuardada.setCliente(cliente);
        tarjetaGuardada.setTarjetaEstado("ACTIVO");
        tarjetaGuardada.setTarjetaCupoTotal(1000.0);
        tarjetaGuardada.setTarjetaCupoDisponible(1000.0);

        // Mock behavior
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(tarjetaGuardada);

        // Act
        Tarjeta resultado = tarjetaService.registrarTarjeta(tarjeta, clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(cliente, resultado.getCliente());
        assertEquals("4111111111111111", resultado.getTarjetaNumero());
        assertEquals("ACTIVO", resultado.getTarjetaEstado());
        assertEquals(1000.0, resultado.getTarjetaCupoTotal());
        assertEquals(1000.0, resultado.getTarjetaCupoDisponible());
    }

    @Test
    void registrarTarjeta_ConClienteInexistente_DebeLanzarExcepcion() {
        // Arrange
        Integer clienteId = 999;
        Tarjeta tarjeta = new Tarjeta();
        
        // Mock behavior
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tarjetaService.registrarTarjeta(tarjeta, clienteId);
        });

        assertEquals("Cliente no encontrado con ID: " + clienteId, exception.getMessage());
    }

    @Test
    void obtenerTodasLasTarjetas_DebeRetornarListaDeTarjetas() {
        // Arrange
        Tarjeta tarjeta1 = new Tarjeta();
        tarjeta1.setTarjetaId(1);
        Tarjeta tarjeta2 = new Tarjeta();
        tarjeta2.setTarjetaId(2);
        List<Tarjeta> tarjetas = Arrays.asList(tarjeta1, tarjeta2);

        when(tarjetaRepository.findAll()).thenReturn(tarjetas);

        // Act
        List<Tarjeta> resultado = tarjetaService.obtenerTodasLasTarjetas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(tarjeta1.getTarjetaId(), resultado.get(0).getTarjetaId());
        assertEquals(tarjeta2.getTarjetaId(), resultado.get(1).getTarjetaId());
    }

    @Test
    void obtenerTarjetasPorClienteId_DebeRetornarTarjetasDelCliente() {
        // Arrange
        Integer clienteId = 1;
        Tarjeta tarjeta1 = new Tarjeta();
        tarjeta1.setTarjetaId(1);
        List<Tarjeta> tarjetas = Arrays.asList(tarjeta1);

        when(tarjetaRepository.findByClienteClienteId(clienteId)).thenReturn(tarjetas);

        // Act
        List<Tarjeta> resultado = tarjetaService.obtenerTarjetasPorClienteId(clienteId);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(tarjeta1.getTarjetaId(), resultado.get(0).getTarjetaId());
    }

    @Test
    void obtenerTarjetaPorId_TarjetaExistente_DebeRetornarTarjeta() {
        // Arrange
        Integer tarjetaId = 1;
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setTarjetaId(tarjetaId);

        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));

        // Act
        Optional<Tarjeta> resultado = tarjetaService.obtenerTarjetaPorId(tarjetaId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(tarjetaId, resultado.get().getTarjetaId());
    }

    @Test
    void actualizarTarjeta_TarjetaExistente_DebeActualizarTarjeta() {
        // Arrange
        Integer tarjetaId = 1;
        Tarjeta tarjetaExistente = new Tarjeta();
        tarjetaExistente.setTarjetaId(tarjetaId);
        tarjetaExistente.setTarjetaEstado("ACTIVO");
        tarjetaExistente.setTarjetaCupoTotal(1000.0);
        tarjetaExistente.setTarjetaCupoDisponible(1000.0);

        Tarjeta tarjetaActualizada = new Tarjeta();
        tarjetaActualizada.setTarjetaEstado("BLOQUEADO");
        tarjetaActualizada.setTarjetaCupoTotal(2000.0);
        tarjetaActualizada.setTarjetaCupoDisponible(2000.0);

        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjetaExistente));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(tarjetaExistente);

        // Act
        Optional<Tarjeta> resultado = tarjetaService.actualizarTarjeta(tarjetaId, tarjetaActualizada);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("BLOQUEADO", resultado.get().getTarjetaEstado());
        assertEquals(2000.0, resultado.get().getTarjetaCupoTotal());
        assertEquals(2000.0, resultado.get().getTarjetaCupoDisponible());
    }

    @Test
    void inactivarTarjeta_TarjetaExistente_DebeInactivarTarjeta() {
        // Arrange
        Integer tarjetaId = 1;
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setTarjetaId(tarjetaId);
        tarjeta.setTarjetaEstado("ACTIVO");

        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(tarjeta);

        // Act
        Optional<Tarjeta> resultado = tarjetaService.inactivarTarjeta(tarjetaId);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals("INACTIVO", resultado.get().getTarjetaEstado());
    }

    @Test
    void actualizarCupoDisponible_TarjetaExistente_DebeActualizarCupo() {
        // Arrange
        Integer tarjetaId = 1;
        double nuevoCupoDisponible = 2000.0;
        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setTarjetaId(tarjetaId);
        tarjeta.setTarjetaCupoDisponible(1000.0);

        when(tarjetaRepository.findById(tarjetaId)).thenReturn(Optional.of(tarjeta));
        when(tarjetaRepository.save(any(Tarjeta.class))).thenReturn(tarjeta);

        // Act
        Optional<Tarjeta> resultado = tarjetaService.actualizarCupoDisponible(tarjetaId, nuevoCupoDisponible);

        // Assert
        assertTrue(resultado.isPresent());
        assertEquals(nuevoCupoDisponible, resultado.get().getTarjetaCupoDisponible());
    }
} 