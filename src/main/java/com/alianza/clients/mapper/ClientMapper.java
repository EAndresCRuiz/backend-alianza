package com.alianza.clients.mapper;

import com.alianza.clients.model.Client;
import com.alianza.clients.dto.ClientDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    ClientDTO toDTO(Client client);
    
    @Mapping(target = "createdAt", ignore = true)    
    Client toEntity(ClientDTO clientDTO);
}