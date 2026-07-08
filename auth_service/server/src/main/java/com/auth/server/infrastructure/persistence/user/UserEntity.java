package com.auth.server.infrastructure.persistence.user;

import com.auth.server.domain.persistence.models.user.Email;
import com.auth.server.domain.persistence.models.user.PasswordHash;
import com.auth.server.domain.persistence.models.user.User;
import com.auth.server.domain.persistence.models.user.UserID;
import com.auth.server.domain.persistence.models.user.Username;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_auth_table")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 64)
  private String id;

  @Column(name = "username", nullable = false, unique = true, length = 150)
  private String username;

  @Column(name = "password", nullable = false, length = 255)
  private String password;

  @Column(name = "email", nullable = false, unique = true, length = 255)
  private String email;

  @Column(name = "is_active", nullable = false)
  private boolean active;

  public static UserEntity fromDomain(User user) {
    return UserEntity.builder()
        .id(user.getId().id())
        .username(user.getUsername().getValue())
        .password(user.getPassword().getValue())
        .email(user.getEmail().getValue())
        .active(user.isActive())
        .build();
  }

  public User toDomain() {
    User user = new User(
        new UserID(id),
        new Username(username),
        new PasswordHash(password),
        new Email(email)
    );
    if (active) {
      user.activate();
    }
    return user;
  }

}
