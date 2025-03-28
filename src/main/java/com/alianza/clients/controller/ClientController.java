package com.alianza.clients.controller;

import com.alianza.clients.dto.ClientDTO;
import com.alianza.clients.service.IClientService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de clientes.
 * Proporciona endpoints para realizar operaciones CRUD sobre clientes.
 */
@Slf4j
@RestController
@RequestMapping("/api/clients")
@Validated
@CrossOrigin(origins = "*")
public class ClientController {

    private final IClientService clientService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param clientService Servicio de clientes
     */
    public ClientController(IClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Obtiene todos los clientes registrados.
     *
     * @return ResponseEntity con la lista de clientes
     */
    @GetMapping
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }
}