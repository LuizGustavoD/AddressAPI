package com.address.service.infrastructure.persistence.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.address.service.infrastructure.persistence.entities.ContactEntity;

public interface ContactJpaRepository extends JpaRepository<ContactEntity, UUID> {

  Optional<ContactEntity> findByUserId(String userId);
}
