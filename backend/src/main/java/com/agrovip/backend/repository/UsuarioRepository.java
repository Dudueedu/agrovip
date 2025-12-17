package com.agrovip.backend.repository;
import com.agrovip.backend.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    Optional<Usuario> findByLoginAndSenha(String login, String senha);
}