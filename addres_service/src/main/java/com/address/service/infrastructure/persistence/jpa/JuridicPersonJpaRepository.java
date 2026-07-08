package com.address.service.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.address.service.infrastructure.persistence.entities.JuridicPersonEntity;

public interface JuridicPersonJpaRepository extends JpaRepository<JuridicPersonEntity, UUID> {

  Optional<JuridicPersonEntity> findByCnpj(String cnpj);
}
