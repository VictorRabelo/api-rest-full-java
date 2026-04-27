package com.devrbl.cleanauth.application.mapper;

import com.devrbl.cleanauth.application.dto.ClientCreateRequestDTO;
import com.devrbl.cleanauth.application.dto.ClientResponseDTO;
import com.devrbl.cleanauth.application.dto.ClientUpdateRequestDTO;
import com.devrbl.cleanauth.domain.entity.Client;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    Client toEntity(ClientCreateRequestDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    void updateEntity(@MappingTarget Client client, ClientUpdateRequestDTO dto);

    ClientResponseDTO toDto(Client client);
}
