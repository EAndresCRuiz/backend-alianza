package com.alianza.clients.service.impl;

import com.alianza.clients.dto.ClientDTO;
import com.alianza.clients.mapper.ClientMapper;
import com.alianza.clients.repository.IClientRepository;
import com.alianza.clients.service.IClientService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

}