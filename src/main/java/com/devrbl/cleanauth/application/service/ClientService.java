package com.devrbl.cleanauth.application.service;

import com.devrbl.cleanauth.application.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    ClientResponseDTO create(ClientCreateRequestDTO dto);
    Page<ClientResponseDTO> list(Pageable pageable);
    ClientResponseDTO getByUuid(String uuid);
    ClientResponseDTO update(String uuid, ClientUpdateRequestDTO dto);
    void delete(String uuid);
}
