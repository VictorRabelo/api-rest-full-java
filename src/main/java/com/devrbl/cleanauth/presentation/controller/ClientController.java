package com.devrbl.cleanauth.presentation.controller;

import com.devrbl.cleanauth.application.dto.*;
import com.devrbl.cleanauth.application.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ClientResponseDTO> create(@Valid @RequestBody ClientCreateRequestDTO dto) {
        return ResponseEntity.ok(clientService.create(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Page<ClientResponseDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(clientService.list(pageable));
    }

    @GetMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ClientResponseDTO> get(@PathVariable String uuid) {
        return ResponseEntity.ok(clientService.getByUuid(uuid));
    }

    @PutMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<ClientResponseDTO> update(@PathVariable String uuid, @Valid @RequestBody ClientUpdateRequestDTO dto) {
        return ResponseEntity.ok(clientService.update(uuid, dto));
    }

    @DeleteMapping("/{uuid}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Void> delete(@PathVariable String uuid) {
        clientService.delete(uuid);
        return ResponseEntity.noContent().build();
    }
}
