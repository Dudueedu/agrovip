package com.agrovip.backend.controller;

import com.agrovip.backend.domain.Usuario;
import com.agrovip.backend.service.UsuarioService; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String login = credentials.get("login");
        String senha = credentials.get("senha");
        
        Optional<Usuario> usuarioOpt = usuarioService.validarLogin(login, senha); 
        
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            return ResponseEntity.ok(Map.of(
                "status", "success", 
                "message", "Login bem-sucedido",
                "usuarioId", usuario.getId(),
                "login", usuario.getLogin(),
                "nome", usuario.getNome(),
                "role", usuario.getRole() 
            ));
        } else {
            return ResponseEntity.status(401).body(Map.of("status", "error", "message", "Login ou senha inválidos"));
        }
    }
}