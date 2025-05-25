package com.edu.cartmaster.controler;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.service.ClienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;

    private Cliente cliente;
    private Map<String, String> credenciales;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setClienteId(1);
        cliente.setClienteNombre("Test User");
        cliente.setClienteCorreo("test@gmail.com");
        cliente.setClienteContrasena("password123");

        credenciales = new HashMap<>();
        credenciales.put("correo", "test@gmail.com");
        credenciales.put("contrasena", "password123");
    }

    @Test
    void registrarCliente_ConDatosValidos_DebeRetornarCreated() throws Exception {
        when(clienteService.registrarCliente(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clienteId").value(cliente.getClienteId()))
                .andExpect(jsonPath("$.clienteNombre").value(cliente.getClienteNombre()))
                .andExpect(jsonPath("$.clienteCorreo").value(cliente.getClienteCorreo()));
    }

    @Test
    void registrarCliente_ConCorreoExistente_DebeRetornarConflict() throws Exception {
        when(clienteService.registrarCliente(any(Cliente.class)))
                .thenThrow(new RuntimeException("El correo ya está registrado"));

        mockMvc.perform(post("/clientes/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isConflict())
                .andExpect(content().string("El correo ya está registrado"));
    }

    @Test
    void login_ConCredencialesValidas_DebeRetornarOk() throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("tipo", "CLIENTE");
        response.put("clienteId", 1);

        when(clienteService.login(credenciales.get("correo"), credenciales.get("contrasena")))
                .thenReturn(response);

        mockMvc.perform(post("/clientes/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("CLIENTE"))
                .andExpect(jsonPath("$.clienteId").value(1));
    }

    @Test
    void login_ConCredencialesInvalidas_DebeRetornarUnauthorized() throws Exception {
        when(clienteService.login(credenciales.get("correo"), credenciales.get("contrasena")))
                .thenReturn(null);

        mockMvc.perform(post("/clientes/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(credenciales)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("CREDENCIALES_INVALIDAS"));
    }

    @Test
    void obtenerTarjetas_ClienteExistente_DebeRetornarTarjetas() throws Exception {
        List<Tarjeta> tarjetas = Arrays.asList(
                new Tarjeta(),
                new Tarjeta()
        );

        when(clienteService.obtenerTarjetasPorClienteId(1)).thenReturn(tarjetas);

        mockMvc.perform(get("/clientes/1/tarjetas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void eliminarCliente_ClienteExistente_DebeRetornarOk() throws Exception {
        doNothing().when(clienteService).eliminarClienteYTarjetas(1);

        mockMvc.perform(delete("/clientes/1"))
                .andExpect(status().isOk());

        verify(clienteService).eliminarClienteYTarjetas(1);
    }

    @Test
    void eliminarCliente_ClienteNoExistente_DebeRetornarNotFound() throws Exception {
        doThrow(new RuntimeException("Cliente no encontrado"))
                .when(clienteService).eliminarClienteYTarjetas(999);

        mockMvc.perform(delete("/clientes/999"))
                .andExpect(status().isNotFound());
    }
} 