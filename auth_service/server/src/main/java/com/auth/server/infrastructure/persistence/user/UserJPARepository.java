package com.auth.server.infrastructure.persistence.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface UserJPARepository extends JpaRepository<UserEntity, String> {

	Optional<UserEntity> findByUsername(String username);

	Optional<UserEntity> findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByEmail(String email);

}
