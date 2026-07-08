package com.address.service.infrastructure.persistence.jpa;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.address.service.infrastructure.persistence.entities.AddressEntity;

public interface AddressJpaRepository extends JpaRepository<AddressEntity, UUID> {

  List<AddressEntity> findByPersonId(UUID personId);
}
