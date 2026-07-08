package com.address.service.infrastructure.persistence.jpa;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.address.service.infrastructure.persistence.entities.PersonEntity;

public interface PersonJpaRepository extends JpaRepository<PersonEntity, UUID> {
}
