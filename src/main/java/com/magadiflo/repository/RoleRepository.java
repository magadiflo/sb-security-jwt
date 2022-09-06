package com.magadiflo.repository;

import com.magadiflo.models.EnumRole;
import com.magadiflo.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(EnumRole name);

}
