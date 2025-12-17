package com.agrovip.backend.service;

import com.agrovip.backend.domain.Usuario;
import com.agrovip.backend.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> validarLogin(String login, String senha) {
        return usuarioRepository.findByLoginAndSenha(login, senha);
    }
    
    
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }
    
    public Optional<Usuario> buscarPorId(Integer id) {
        return usuarioRepository.findById(id);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public void deletar(Integer id) {
        usuarioRepository.deleteById(id);
    }
    
    public boolean existe(Integer id) {
        return usuarioRepository.existsById(id);
    }
}