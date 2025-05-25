package com.edu.cartmaster.controler;

import com.edu.cartmaster.model.Cliente;
import com.edu.cartmaster.model.Tarjeta;
import com.edu.cartmaster.service.TarjetaService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TarjetaController.class)
class TarjetaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TarjetaService tarjetaService;

    @Autowired
    private ObjectMapper objectMapper;

    private Tarjeta tarjeta;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setClienteId(1);
        cliente.setClienteNombre("Test User");
        cliente.setClienteCorreo("test@gmail.com");

        tarjeta = new Tarjeta();
        tarjeta.setTarjetaId(1);
        tarjeta.setTarjetaNumero("4111111111111111");
        tarjeta.setTarjetaEstado("ACTIVO");
        tarjeta.setTarjetaCupoTotal(1000.0);
        tarjeta.setTarjetaCupoDisponible(1000.0);
        tarjeta.setCliente(cliente);
    }

    @Test
    void obtenerTodasLasTarjetas_DebeRetornarListaDeTarjetas() throws Exception {
        List<Tarjeta> tarjetas = Arrays.asList(tarjeta);
        when(tarjetaService.obtenerTodasLasTarjetas()).thenReturn(tarjetas);

        mockMvc.perform(get("/api/tarjetas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tarjetaNumero").value(tarjeta.getTarjetaNumero()));
    }

    @Test
    void obtenerTarjetasPorCliente_DebeRetornarTarjetasDelCliente() throws Exception {
        List<Tarjeta> tarjetas = Arrays.asList(tarjeta);
        when(tarjetaService.obtenerTarjetasPorClienteId(1)).thenReturn(tarjetas);

        mockMvc.perform(get("/api/tarjetas/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].tarjetaNumero").value(tarjeta.getTarjetaNumero()));
    }

    @Test
    void obtenerTarjetaPorId_TarjetaExistente_DebeRetornarTarjeta() throws Exception {
        when(tarjetaService.obtenerTarjetaPorId(1)).thenReturn(Optional.of(tarjeta));

        mockMvc.perform(get("/api/tarjetas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarjetaNumero").value(tarjeta.getTarjetaNumero()));
    }

    @Test
    void obtenerTarjetaPorId_TarjetaNoExistente_DebeRetornarNotFound() throws Exception {
        when(tarjetaService.obtenerTarjetaPorId(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tarjetas/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearTarjeta_DatosValidos_DebeRetornarCreated() throws Exception {
        when(tarjetaService.registrarTarjeta(any(Tarjeta.class), eq(1))).thenReturn(tarjeta);

        mockMvc.perform(post("/api/tarjetas/crear/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarjeta)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tarjetaNumero").value(tarjeta.getTarjetaNumero()));
    }

    @Test
    void actualizarTarjeta_TarjetaExistente_DebeRetornarOk() throws Exception {
        when(tarjetaService.actualizarTarjeta(eq(1), any(Tarjeta.class))).thenReturn(Optional.of(tarjeta));

        mockMvc.perform(put("/api/tarjetas/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarjeta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarjetaNumero").value(tarjeta.getTarjetaNumero()));
    }

    @Test
    void actualizarTarjetaConCupos_TarjetaExistente_DebeRetornarOk() throws Exception {
        when(tarjetaService.actualizarTarjetaConCupos(eq(1), any(Tarjeta.class))).thenReturn(Optional.of(tarjeta));

        mockMvc.perform(put("/api/tarjetas/actualizar_con_cupos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarjeta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarjetaNumero").value(tarjeta.getTarjetaNumero()));
    }

    @Test
    void actualizarDatosGenerales_TarjetaExistente_DebeRetornarOk() throws Exception {
        when(tarjetaService.actualizarDatosGeneralesTarjeta(eq(1), any(Tarjeta.class))).thenReturn(Optional.of(tarjeta));

        mockMvc.perform(put("/api/tarjetas/actualizar_generales/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tarjeta)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarjetaNumero").value(tarjeta.getTarjetaNumero()));
    }

    @Test
    void inactivarTarjeta_TarjetaExistente_DebeRetornarOk() throws Exception {
        tarjeta.setTarjetaEstado("INACTIVO");
        when(tarjetaService.inactivarTarjeta(1)).thenReturn(Optional.of(tarjeta));

        mockMvc.perform(delete("/api/tarjetas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarjetaEstado").value("INACTIVO"));
    }

    @Test
    void actualizarCupoDisponible_TarjetaExistente_DebeRetornarOk() throws Exception {
        Map<String, Double> request = new HashMap<>();
        request.put("tarjetaCupoDisponible", 2000.0);

        tarjeta.setTarjetaCupoDisponible(2000.0);
        when(tarjetaService.actualizarCupoDisponible(eq(1), eq(2000.0))).thenReturn(Optional.of(tarjeta));

        mockMvc.perform(put("/api/tarjetas/actualizar_cupo_disponible/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tarjetaCupoDisponible").value(2000.0));
    }

} 