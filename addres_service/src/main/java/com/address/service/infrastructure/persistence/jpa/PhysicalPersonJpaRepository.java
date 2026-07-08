package com.address.service.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.address.service.infrastructure.persistence.entities.PhysicalPersonEntity;

public interface PhysicalPersonJpaRepository extends JpaRepository<PhysicalPersonEntity, UUID> {

  Optional<PhysicalPersonEntity> findByCpf(String cpf);
}
