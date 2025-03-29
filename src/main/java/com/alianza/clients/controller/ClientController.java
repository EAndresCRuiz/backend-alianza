package com.alianza.clients.controller;

import com.alianza.clients.dto.ClientDTO;
import com.alianza.clients.dto.ClientSearchCriteria;
import com.alianza.clients.service.IClientService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import com.alianza.clients.exception.ExportException;

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

    /**
     * Busca clientes por el campo sharedKey.
     *
     * @param sharedKey sharedKey a buscar
     * @return ResponseEntity con la lista de clientes que coinciden
     */
    @GetMapping("/search")
    public ResponseEntity<List<ClientDTO>> searchClientsBySharedKey(@RequestParam String sharedKey) {
        return ResponseEntity.ok(clientService.searchClientsBySharedKey(sharedKey));
    }

    /**
     * Crea un nuevo cliente.
     *
     * @param clientDTO DTO con la información del cliente a crear
     * @return ResponseEntity con el cliente creado
     */
    @PostMapping
    public ResponseEntity<ClientDTO> createClient(@Valid @RequestBody ClientDTO clientDTO) {
        return new ResponseEntity<>(clientService.createClient(clientDTO), HttpStatus.CREATED);
    }

    /**
     * Realiza una búsqueda avanzada de clientes según los criterios especificados.
     *
     * @param criteria Criterios de búsqueda
     * @return ResponseEntity con la lista de clientes que coinciden con los criterios
     */
    @PostMapping("/search/advanced")
    public ResponseEntity<List<ClientDTO>> searchClients(@RequestBody ClientSearchCriteria criteria) {
        return ResponseEntity.ok(clientService.searchClients(criteria));
    }

    /**
     * Exporta los clientes que coinciden con los criterios de búsqueda al formato especificado.
     *
     * @param criteria Criterios de búsqueda y formato de exportación
     * @return ResponseEntity con el archivo exportado
     */
    @PostMapping("/export")
    public ResponseEntity<Resource> exportClients(@RequestBody ClientSearchCriteria criteria) {
        try {
            Resource resource = clientService.exportClients(criteria);
            String filename = "clients." + criteria.getExportFormat().toLowerCase();
            String contentType = criteria.getExportFormat().equalsIgnoreCase("CSV") ?
                    "text/csv" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (IOException e) {
            log.error("Error al exportar clientes: {}", e.getMessage(), e);
            throw new ExportException("Error al exportar clientes: " + e.getMessage());
        }
    }
}