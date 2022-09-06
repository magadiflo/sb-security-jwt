package com.magadiflo.services;

import com.magadiflo.models.EnumRole;
import com.magadiflo.models.Role;
import com.magadiflo.models.User;

import java.util.Optional;

public interface IUserService {

    //User
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    //Role
    Optional<Role> findByName(EnumRole name);

}
