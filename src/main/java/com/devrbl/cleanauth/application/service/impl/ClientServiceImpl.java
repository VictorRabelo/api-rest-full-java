package com.devrbl.cleanauth.application.service.impl;

import com.devrbl.cleanauth.application.dto.*;
import com.devrbl.cleanauth.application.mapper.ClientMapper;
import com.devrbl.cleanauth.application.service.ClientService;
import com.devrbl.cleanauth.domain.entity.Client;
import com.devrbl.cleanauth.domain.repository.ClientRepository;
import com.victor.cleanauth.shared.exception.NotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    @Override
    @Transactional
    public ClientResponseDTO create(ClientCreateRequestDTO dto) {
        Client client = mapper.toEntity(dto);
        client = repository.save(client);
        log.info("Created client {}", client.getUuid());
        return mapper.toDto(client);
    }

    @Override
    public Page<ClientResponseDTO> list(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    public ClientResponseDTO getByUuid(String uuid) {
        Client client = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        return mapper.toDto(client);
    }

    @Override
    @Transactional
    public ClientResponseDTO update(String uuid, ClientUpdateRequestDTO dto) {
        Client client = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        mapper.updateEntity(client, dto);
        client = repository.save(client);
        log.info("Updated client {}", client.getUuid());
        return mapper.toDto(client);
    }

    @Override
    @Transactional
    public void delete(String uuid) {
        Client client = repository.findByUuid(uuid)
                .orElseThrow(() -> new NotFoundException("Client not found"));
        repository.delete(client);
        log.info("Deleted client {}", client.getUuid());
    }
}
