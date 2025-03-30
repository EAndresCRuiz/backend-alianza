package com.alianza.clients.controller;

import com.alianza.clients.dto.ClientDTO;
import com.alianza.clients.dto.ClientSearchCriteria;
import com.alianza.clients.exception.ExportException;
import com.alianza.clients.exception.ResourceNotFoundException;
import com.alianza.clients.service.IClientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IClientService clientService;

    @Autowired
    private ObjectMapper objectMapper;

    private ClientDTO clientDTO;
    private List<ClientDTO> clientDTOList;
    private ClientSearchCriteria searchCriteria;

    @BeforeEach
    void setUp() {
        clientDTO = ClientDTO.builder()
                .id("1")
                .name("Test Client")
                .email("test@example.com")
                .phone("1234567890")
                .sharedKey("test")
                .createdAt(LocalDate.now())
                .build();

        clientDTOList = Arrays.asList(clientDTO);

        searchCriteria = ClientSearchCriteria.builder()
                .name("Test")
                .email("test@example.com")
                .phone("1234567890")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now())
                .exportFormat("CSV")
                .build();
    }

    @Test
    void getAllClients_ShouldReturnAllClients() throws Exception {
        // Arrange
        when(clientService.getAllClients()).thenReturn(clientDTOList);

        // Act & Assert
        mockMvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("Test Client")))
                .andExpect(jsonPath("$[0].email", is("test@example.com")));

        verify(clientService).getAllClients();
    }

    @Test
    void searchClientsBySharedKey_WithValidKey_ShouldReturnMatchingClients() throws Exception {
        // Arrange
        String sharedKey = "test";
        when(clientService.searchClientsBySharedKey(sharedKey)).thenReturn(clientDTOList);

        // Act & Assert
        mockMvc.perform(get("/api/clients/search")
                .param("sharedKey", sharedKey))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].sharedKey", is("test")));

        verify(clientService).searchClientsBySharedKey(sharedKey);
    }

    @Test
    void searchClientsBySharedKey_WithInvalidKey_ShouldReturnNotFound() throws Exception {
        // Arrange
        String sharedKey = "nonexistent";
        when(clientService.searchClientsBySharedKey(sharedKey))
                .thenThrow(new ResourceNotFoundException("Cliente", "sharedKey", sharedKey));

        // Act & Assert
        mockMvc.perform(get("/api/clients/search")
                .param("sharedKey", sharedKey))
                .andExpect(status().isNotFound());

        verify(clientService).searchClientsBySharedKey(sharedKey);
    }

    @Test
    void createClient_WithValidData_ShouldReturnCreatedClient() throws Exception {
        // Arrange
        when(clientService.createClient(any(ClientDTO.class))).thenReturn(clientDTO);

        // Act & Assert
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clientDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("Test Client")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(clientService).createClient(any(ClientDTO.class));
    }

    @Test
    void createClient_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ClientDTO invalidClient = ClientDTO.builder()
                .id("1")
                .name("")
                .email("invalid-email")
                .phone("123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/clients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidClient)))
                .andExpect(status().isBadRequest());

        verify(clientService, never()).createClient(any(ClientDTO.class));
    }

    @Test
    void searchClients_WithValidCriteria_ShouldReturnMatchingClients() throws Exception {
        // Arrange
        when(clientService.searchClients(any(ClientSearchCriteria.class))).thenReturn(clientDTOList);

        // Act & Assert
        mockMvc.perform(post("/api/clients/search/advanced")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Test Client")));

        verify(clientService).searchClients(any(ClientSearchCriteria.class));
    }

    @Test
    void searchClients_WithNoMatches_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(clientService.searchClients(any(ClientSearchCriteria.class))).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(post("/api/clients/search/advanced")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(clientService).searchClients(any(ClientSearchCriteria.class));
    }

    @Test
    void exportClients_WithValidCriteria_ShouldReturnResource() throws Exception {
        // Arrange
        byte[] csvData = "id,name,email,phone,sharedKey\n1,Test Client,test@example.com,1234567890,test".getBytes();
        Resource resource = new ByteArrayResource(csvData);
        
        when(clientService.exportClients(any(ClientSearchCriteria.class))).thenReturn(resource);

        // Act & Assert
        mockMvc.perform(post("/api/clients/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"clients.csv\""))
                .andExpect(content().contentType("text/csv"));

        verify(clientService).exportClients(any(ClientSearchCriteria.class));
    }

    @Test
    void exportClients_WithExcelFormat_ShouldReturnExcelResource() throws Exception {
        // Arrange
        searchCriteria.setExportFormat("EXCEL");
        byte[] excelData = new byte[100];
        Resource resource = new ByteArrayResource(excelData);
        
        when(clientService.exportClients(any(ClientSearchCriteria.class))).thenReturn(resource);

        // Act & Assert
        mockMvc.perform(post("/api/clients/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"clients.excel\""))
                .andExpect(content().contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

        verify(clientService).exportClients(any(ClientSearchCriteria.class));
    }

    @Test
    void exportClients_WithIOException_ShouldThrowExportException() throws Exception {
        // Arrange
        when(clientService.exportClients(any(ClientSearchCriteria.class))).thenThrow(new java.io.IOException("Export failed"));

        // Act & Assert
        mockMvc.perform(post("/api/clients/export")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(searchCriteria)))
                .andExpect(status().isInternalServerError());

        verify(clientService).exportClients(any(ClientSearchCriteria.class));
    }
}