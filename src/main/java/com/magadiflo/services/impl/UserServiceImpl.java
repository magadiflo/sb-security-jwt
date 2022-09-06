package com.magadiflo.services.impl;

import com.magadiflo.models.EnumRole;
import com.magadiflo.models.Role;
import com.magadiflo.models.User;
import com.magadiflo.repository.RoleRepository;
import com.magadiflo.repository.UserRepository;
import com.magadiflo.services.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public Boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findByName(EnumRole name) {
        return this.roleRepository.findByName(name);
    }
}
