package com.alianza.clients.service;

import com.alianza.clients.dto.ClientDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interfaz que define los servicios disponibles para la gestión de clientes.
 */
@Service
public interface IClientService {

    /**
     * Obtiene todos los clientes registrados en el sistema.
     *
     * @return Lista de DTOs de clientes
     */
    public List<ClientDTO> getAllClients();

}