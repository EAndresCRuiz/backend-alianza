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

    /**
     * Busca clientes por su sharedKey.
     *
     * @param sharedKey sharedKey a buscar
     * @return Lista de DTOs de clientes que coinciden con la búsqueda
     */
    public List<ClientDTO> searchClientsBySharedKey(String sharedKey);

    /**
     * Crea un nuevo cliente en el sistema.
     *
     * @param clientDTO DTO con la información del cliente a crear
     * @return DTO del cliente creado
     */
    public ClientDTO createClient(ClientDTO clientDTO);
}