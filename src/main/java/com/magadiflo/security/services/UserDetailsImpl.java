package com.magadiflo.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.magadiflo.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Si el proceso de autenticación es exitoso, podemos obtener información del usuario,
 * como nombre de usuario, contraseña, autoridades de un objeto de Authentication.
 * Ejm.
 * Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
 * UserDetails userDetails = (UserDetails) authentication.getPrincipal();
 * // userDetails.getUsername()
 * // userDetails.getPassword()
 * // userDetails.getAuthorities()
 * <p>
 * Ahora, si queremos obtener más datos (id, email, etc...), podemos crear una implementación de esa interfaz UserDetails
 */

public class UserDetailsImpl implements UserDetails {

    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(Long id, String username, String email, String password,
                           Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toList());
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getEmail(), user.getPassword(), authorities);
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDetailsImpl user = (UserDetailsImpl) o;
        return this.id.equals(user.id);
    }
}
