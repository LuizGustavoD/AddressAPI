package com.auth.server.domain.persistence.repository;

import com.auth.server.domain.persistence.models.user.User;
import com.auth.server.domain.persistence.models.user.UserID;


public interface UserRepository {

  void save(User user);
  
  User findById(UserID id);
  
  User findByUsername(String username);
  
  User findByEmail(String email);
  
  void update(User user);
  
  void delete(UserID id);
  
  boolean existsById(UserID id);
  
  boolean existsByUsername(String username);
  
  boolean existsByEmail(String email);
}
