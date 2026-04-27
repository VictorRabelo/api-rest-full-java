package com.devrbl.cleanauth.service;

import com.devrbl.cleanauth.application.dto.ClientCreateRequestDTO;
import com.devrbl.cleanauth.application.dto.ClientResponseDTO;
import com.devrbl.cleanauth.application.mapper.ClientMapper;
import com.devrbl.cleanauth.application.service.impl.ClientServiceImpl;
import com.devrbl.cleanauth.domain.entity.Client;
import com.devrbl.cleanauth.domain.repository.ClientRepository;
import com.devrbl.cleanauth.shared.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    ClientRepository repository;
    @Mock
    ClientMapper mapper;
    @InjectMocks
    ClientServiceImpl service;

    @Test
    void createShouldReturnDto() {
        ClientCreateRequestDTO dto = new ClientCreateRequestDTO("John", "j@e.com", "123", "2000-01-01");
        Client entity = Client.builder().id(1L).uuid("u").build();
        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        ClientResponseDTO response = new ClientResponseDTO("u", null, null, null, null, null, null);
        when(mapper.toDto(entity)).thenReturn(response);
        ClientResponseDTO result = service.create(dto);
        assertEquals("u", result.uuid());
    }

    @Test
    void getByUuidNotFoundThrows() {
        when(repository.findByUuid("x")).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getByUuid("x"));
    }
}
