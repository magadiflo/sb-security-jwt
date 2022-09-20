package com.magadiflo.resources;

import com.magadiflo.exceptions.TokenRefreshException;
import com.magadiflo.models.EnumRole;
import com.magadiflo.models.RefreshToken;
import com.magadiflo.models.Role;
import com.magadiflo.models.User;
import com.magadiflo.payload.request.LoginRequest;
import com.magadiflo.payload.request.SignupRequest;
import com.magadiflo.payload.request.TokenRefreshRequest;
import com.magadiflo.payload.response.JwtResponse;
import com.magadiflo.payload.response.MessageResponse;
import com.magadiflo.payload.response.TokenRefreshResponse;
import com.magadiflo.security.jwt.JwtUtils;
import com.magadiflo.security.services.UserDetailsImpl;
import com.magadiflo.services.IRefreshTokenService;
import com.magadiflo.services.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthResource {
    private final AuthenticationManager authenticationManager;
    private final IUserService userService;
    private final IRefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthResource(AuthenticationManager authenticationManager, IUserService userService, IRefreshTokenService refreshTokenService,
                        PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<?> authenticationUser(@Valid @RequestBody LoginRequest loginRequest) {
        /**
         *  UsernamePasswordAuthenticationToken obtiene {nombre de usuario, contraseña} de la solicitud
         *  de inicio de sesión, AuthenticationManager lo usará para autenticar una cuenta de inicio de sesión.
         *
         *  AuthenticationManager tiene un DaoAuthenticationProvider (con la ayuda de UserDetailsService y PasswordEncoder)
         *  para validar el objeto UsernamePasswordAuthenticationToken. Si tiene éxito, AuthenticationManager devuelve
         *  un objeto de autenticación completo (incluidas las autorizaciones otorgadas).
         */
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        Authentication authentication = this.authenticationManager.authenticate(authenticationToken);

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = this.jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        RefreshToken refreshToken = this.refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(jwt, refreshToken.getToken(),userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @PostMapping(path = "/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (this.userService.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: username is already taken!"));
        }
        if (this.userService.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Email is already in use!"));
        }
        //Create new a user's count
        User user = new User(signupRequest.getUsername(), signupRequest.getEmail(), this.passwordEncoder.encode(signupRequest.getPassword()));
        Set<String> strRoles = signupRequest.getRoles();
        Set<Role> roles = new HashSet<>();
        if (strRoles == null) {
            Role userRole = this.userService.findByName(EnumRole.ROLE_USER).orElseThrow(() -> new RuntimeException("Error: Role is not found"));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = this.userService.findByName(EnumRole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = this.userService.findByName(EnumRole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = this.userService.findByName(EnumRole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        this.userService.saveUser(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping(path = "/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        return this.refreshTokenService.findByToken(requestRefreshToken)
                .map(this.refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String token = this.jwtUtils.generateTokenFromUsername(user.getUsername());
                    return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!"));
    }


}
