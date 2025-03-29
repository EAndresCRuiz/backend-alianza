package com.alianza.clients.service.impl;

import com.alianza.clients.dto.ClientDTO;
import com.alianza.clients.dto.ClientSearchCriteria;
import com.alianza.clients.exception.DuplicateKeyException;
import com.alianza.clients.exception.ResourceNotFoundException;
import com.alianza.clients.mapper.ClientMapper;
import com.alianza.clients.model.Client;
import com.alianza.clients.repository.IClientRepository;
import com.alianza.clients.repository.specification.ClientSpecification;
import com.alianza.clients.service.IClientService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.UUID;

/**
 * Servicios de gestión de clientes.
 */
@Service
public class ClientServiceImpl implements IClientService {
    private static final Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    private static final String[] CSV_HEADERS = {"ID", "Shared Key", "Business ID", "Email", "Phone", "Created At"};
    private static final String[] EXCEL_HEADERS = {"ID", "Shared Key", "Business ID", "Email", "Phone", "Created At"};

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
        } catch (DuplicateKeyException e) {
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

    @Override
    public List<ClientDTO> searchClients(ClientSearchCriteria criteria) {
        logger.info("Searching clients with criteria: {}", criteria);

        Specification<Client> spec = Specification.where(ClientSpecification.hasName(criteria.getName()))
                .and(ClientSpecification.hasEmail(criteria.getEmail()))
                .and(ClientSpecification.hasPhone(criteria.getPhone()))
                .and(ClientSpecification.createdBetween(criteria.getStartDate(), criteria.getEndDate()));

        return clientRepository.findAll(spec).stream()
                .map(clientMapper::toDTO)
                .toList();
    }

    @Override
    public Resource exportClients(ClientSearchCriteria criteria) throws IOException {
        List<ClientDTO> clients = searchClients(criteria);
        
        if ("CSV".equalsIgnoreCase(criteria.getExportFormat())) {
            return exportToCSV(clients);
        } else if ("EXCEL".equalsIgnoreCase(criteria.getExportFormat())) {
            return exportToExcel(clients);
        } else {
            throw new IllegalArgumentException("Formato de exportación no soportado: " + criteria.getExportFormat());
        }
    }

    private Resource exportToCSV(List<ClientDTO> clients) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), CSVFormat.DEFAULT.withHeader(CSV_HEADERS))) {
            for (ClientDTO client : clients) {
                csvPrinter.printRecord(
                    client.getId(),
                    client.getSharedKey(),
                    client.getEmail(),
                    client.getPhone(),
                    client.getCreatedAt()
                );
            }
        }
        return new ByteArrayResource(out.toByteArray());
    }

    private Resource exportToExcel(List<ClientDTO> clients) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Clients");
            
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < EXCEL_HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(EXCEL_HEADERS[i]);
            }

            int rowNum = 1;
            for (ClientDTO client : clients) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(client.getId());
                row.createCell(1).setCellValue(client.getSharedKey());
                row.createCell(3).setCellValue(client.getEmail());
                row.createCell(4).setCellValue(client.getPhone());
                row.createCell(5).setCellValue(client.getCreatedAt() != null ? client.getCreatedAt().toString() : "");
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return new ByteArrayResource(out.toByteArray());
        }
    }
}