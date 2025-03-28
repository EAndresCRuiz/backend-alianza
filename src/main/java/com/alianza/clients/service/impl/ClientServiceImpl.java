package com.alianza.clients.service.impl;

import com.alianza.clients.dto.ClientDTO;
import com.alianza.clients.exception.DuplicateKeyException;
import com.alianza.clients.exception.ResourceNotFoundException;
import com.alianza.clients.mapper.ClientMapper;
import com.alianza.clients.model.Client;
import com.alianza.clients.repository.IClientRepository;
import com.alianza.clients.service.IClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Servicios de gestión de clientes.
 */
@Service
public class ClientServiceImpl implements IClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    private final IClientRepository clientRepository;
    private final ClientMapper clientMapper;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param clientRepository Repositorio de clientes
     * @param clientMapper Mapeador entre entidad y DTO
     */
    @Autowired
    public ClientServiceImpl(IClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    @Override
    public List<ClientDTO> getAllClients() {
        logger.info("Fetching all clients");
        return clientRepository.findAll().stream()
                .map(clientMapper::toDTO)
                .toList();
    }

    @Override
    public List<ClientDTO> searchClientsBySharedKey(String sharedKey) {
        logger.info("Searching clients with shared key: {}", sharedKey);
        List<Client> clients = clientRepository.findBySharedKeyContainingIgnoreCase(sharedKey);

        if (clients.isEmpty()) {
            throw new ResourceNotFoundException("Cliente", "sharedKey", sharedKey);
        }

        return clients.stream()
                .map(clientMapper::toDTO)
                .toList();
    }

    @Override
    public ClientDTO createClient(ClientDTO clientDTO) {
        logger.info("Creating new client with email: {}", clientDTO.getEmail());

        String sharedKey = generateSharedKey(clientDTO.getEmail());

        if (clientRepository.existsBySharedKey(sharedKey)) {
            throw new DuplicateKeyException("El sharedKey '" + sharedKey + "' ya existe. Por favor use un email diferente.");
        }

        clientDTO.setSharedKey(sharedKey);

        if (clientDTO.getId() == null || clientDTO.getId().isEmpty()) {
            clientDTO.setId(UUID.randomUUID().toString());
        }

        try {
            Client client = clientMapper.toEntity(clientDTO);
            Client savedClient = clientRepository.save(client);
            return clientMapper.toDTO(savedClient);
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation when saving client", e);
            throw new DuplicateKeyException("Error al guardar el cliente. Posible duplicación de datos.");
        }
    }

    /**
     * Genera el sharedKey a partir del email del cliente.
     * Se genera tomando la parte del email antes del símbolo @.
     *
     * @param email Email del cliente
     * @return sharedKey generada
     * @throws IllegalArgumentException si el email es nulo o no tiene formato válido
     */
    private String generateSharedKey(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("El email no es válido para generar sharedKey.");
        }
        return email.split("@")[0].toLowerCase();
    }

}