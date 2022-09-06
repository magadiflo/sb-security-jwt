package com.magadiflo.repository;

import com.magadiflo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// Recordar que no es necesario anotarlo con @Repository,
// ya que al extender de JpaRepository en automático la interfaz es manejado por el contenedor de spring
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
