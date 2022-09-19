package com.magadiflo.repository;

import com.magadiflo.models.RefreshToken;
import com.magadiflo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    int deleteByUser(User user);

}
/**
 * NOTA: El método de arriba, deleteByUser(...) estaba anotado con @Modifying, pero según investigué esto solo se aplica a
 * los métodos que usan la anotación @Query(...) para hacer la consulta cuyas instrucciones incluyen:
 * INSERT, UPDATE, DELETE y DDL
 *
 * @Modifying
 * Indica que se debe considerar que un método de consulta modifica la consulta, ya que cambia la forma en que debe ejecutarse.
 * Esta anotación solo se considera si se usa en métodos de consulta definidos a través @Query annotation.
 * No se aplica en métodos de implementación personalizados o consultas derivadas del nombre del método, porque tienen
 * control sobre las API de acceso a datos subyacentes o especifican si se modifican por su nombre.
 */