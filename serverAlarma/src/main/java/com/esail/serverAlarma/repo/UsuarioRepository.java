package com.esail.serverAlarma.repo;

import com.esail.serverAlarma.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 // ---> Este directorio es donde se encuentran los ficheros para la conexión a la BD
 */

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByUsername(String username);
}

/**
 * Esto me da acceso a metodos como:
 * -> findAll()
 * -> findById()
 * -> save()
 * -> delete()
 */
