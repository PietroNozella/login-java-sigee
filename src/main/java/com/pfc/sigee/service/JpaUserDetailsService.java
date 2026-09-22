package com.pfc.sigee.service;

import com.pfc.sigee.entity.Usuario;
import com.pfc.sigee.repository.UsuarioRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
        // Conta travada nem testa a senha: o Spring barra antes com LockedException
        // (→ /login?bloqueado), sem gastar BCrypt nem vazar tempo de resposta.
        return new User(
                usuario.getUsername(),
                usuario.getSenha(),
                usuario.isAtivo(),
                true, true,
                !usuario.bloqueado(),
                List.of(new SimpleGrantedAuthority(usuario.getPerfil().autoridade())));
    }
}
