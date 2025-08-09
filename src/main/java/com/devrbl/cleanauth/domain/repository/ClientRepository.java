package com.devrbl.cleanauth.domain.repository;

import com.devrbl.cleanauth.domain.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUuid(String uuid);
}
