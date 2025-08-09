package com.devrbl.cleanauth.application.mapper;

import com.devrbl.cleanauth.application.dto.ClientCreateRequestDTO;
import com.devrbl.cleanauth.application.dto.ClientResponseDTO;
import com.devrbl.cleanauth.application.dto.ClientUpdateRequestDTO;
import com.devrbl.cleanauth.domain.entity.Client;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ClientMapper {
    Client toEntity(ClientCreateRequestDTO dto);
    void updateEntity(@MappingTarget Client client, ClientUpdateRequestDTO dto);
    ClientResponseDTO toDto(Client client);
}
