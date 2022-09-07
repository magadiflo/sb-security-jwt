package com.magadiflo.security.jwt;

import com.magadiflo.security.services.UserDetailsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * OncePerRequestFilter realiza una sola ejecución por cada solicitud a nuestra API. Proporciona
 * un método doFilterInternal() que implementaremos analizando y validando JWT, cargando los detalles
 * del usuario (usando UserDetailsService), verificando la autorización (usando UsernamePasswordAuthenticationToken).
 */

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthTokenFilter.class);

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        //Obtenemos el jwt desde el encabezado de autorización (quitando el prefijo "Bearer ")
        String jwt = this.parseJwt(request);
        try {
            if (jwt != null && this.jwtUtils.validateJwtToken(jwt)) {
                //Como el jwt fue válido obtenemos el username a partir de él
                String username = this.jwtUtils.getUsernameFromJwtToken(jwt);
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                /**
                 * >>>> CONOCIENDO EL OBJETO WebAuthenticationDetailsSource()
                 * WebAuthenticationDetailsSource(), Tiene la única responsabilidad de convertir una instancia
                 * de la clase HttpServletRequest en una instancia de la clase WebAuthenticationDetails.
                 * Puedes pensar en ello como un simple convertidor.
                 *
                 * El objeto HttpServletRequest que representa los datos HTTP sin procesar y analizar es una
                 * clase Java estándar es la entrada. Y WebAuthenticationDetails es una clase Spring interna.
                 *
                 * Por lo tanto, puede considerarlo como un puente entre las clases de servlet y las clases de Spring.
                 *
                 * >>>> ¿POR QUÉ NO LO USAREMOS?
                 * En el tutorial está esta línea de código y según investigué este no sería necesario, ya que no estamos
                 * usando sessiones sino una autenticación por token.
                 *
                 * "Parece que esta línea es para cargar la información de la sesión para la solicitud actual del
                 * lado del servidor. Sin embargo, como todos sabemos, este proyecto se basa en la autenticación
                 * basada en token y, por lo tanto, cargar la sesión en el servidor ya no sirve (supongo que cargar
                 * la sesión aquí es esencial si usamos la autenticación tradicional session_id)."
                 */
                //authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            LOGGER.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring("Bearer ".length());
        }
        return null;
    }

}
