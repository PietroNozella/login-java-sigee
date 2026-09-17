package com.pfc.thindesk.service;

import com.pfc.thindesk.entity.Perfil;
import com.pfc.thindesk.entity.PasswordResetToken;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.PasswordResetTokenRepository;
import com.pfc.thindesk.repository.UsuarioRepository;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.recovery.token-minutos:60}")
    private long tokenMinutos;

    @Value("${app.login.max-tentativas:5}")
    private int maxTentativas;

    @Value("${app.login.bloqueio-minutos:15}")
    private long bloqueioMinutos;

    // Cadastro controlado: só Administrador chama (verificado no controller).
    // Conta nasce ativa, sem privilégio extra — igual ao SIGEE.
    public Usuario cadastrar(String nome, String sobrenome, String email,
                             String username, String senhaInicial, Perfil perfil) {
        if (usuarioRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Nome de usuário já existe.");
        }
        if (usuarioRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("E-mail já cadastrado.");
        }
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setSobrenome(sobrenome);
        usuario.setEmail(email);
        usuario.setUsername(username);
        usuario.setSenha(passwordEncoder.encode(senhaInicial));
        usuario.setPerfil(perfil);
        usuario.setAtivo(true);
        usuario.setCriadoEm(Instant.now());
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    public void trocarSenha(String username, String senhaAtual, String senhaNova) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        if (!passwordEncoder.matches(senhaAtual, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha atual incorreta.");
        }
        usuario.setSenha(passwordEncoder.encode(senhaNova));
        usuarioRepository.save(usuario);
    }

    // Gera token de uso único. Sem servidor de e-mail no PFC, o token é
    // exibido uma vez na tela de confirmação (e registrado em log).
    public PasswordResetToken solicitarRecovery(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario == null) {
            return null; // resposta neutra: não revela se o e-mail existe
        }
        PasswordResetToken reset = new PasswordResetToken();
        reset.setToken(UUID.randomUUID().toString().replace("-", ""));
        reset.setUsuarioId(usuario.getId());
        reset.setExpiraEm(Instant.now().plus(Duration.ofMinutes(tokenMinutos)));
        reset.setUsado(false);
        return tokenRepository.save(reset);
    }

    public void redefinirSenha(String token, String senhaNova) {
        PasswordResetToken reset = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Link inválido."));
        if (reset.isUsado() || reset.expirado()) {
            throw new IllegalArgumentException("Link expirado ou já utilizado.");
        }
        Usuario usuario = usuarioRepository.findById(reset.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        usuario.setSenha(passwordEncoder.encode(senhaNova));
        usuario.setFalhasLogin(0);
        usuario.setBloqueadoAte(null);
        usuarioRepository.save(usuario);
        reset.setUsado(true);
        tokenRepository.save(reset);
    }

    // Retorna true se a conta acabou de ser bloqueada nesta tentativa.
    public boolean registrarFalha(String username) {
        return usuarioRepository.findByUsername(username).map(usuario -> {
            usuario.setFalhasLogin(usuario.getFalhasLogin() + 1);
            boolean bloqueouAgora = false;
            if (usuario.getFalhasLogin() >= maxTentativas) {
                usuario.setBloqueadoAte(Instant.now().plus(Duration.ofMinutes(bloqueioMinutos)));
                usuario.setFalhasLogin(0);
                bloqueouAgora = true;
            }
            usuarioRepository.save(usuario);
            return bloqueouAgora;
        }).orElse(false);
    }

    public void registrarSucesso(String username) {
        usuarioRepository.findByUsername(username).ifPresent(usuario -> {
            usuario.setFalhasLogin(0);
            usuario.setBloqueadoAte(null);
            usuarioRepository.save(usuario);
        });
    }

    public void registrarAceite(String username, String termosVersao, String privacidadeVersao) {
        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        usuario.setAceiteTermosVersao(termosVersao);
        usuario.setAceitePrivacidadeVersao(privacidadeVersao);
        usuario.setAceiteEm(Instant.now());
        usuarioRepository.save(usuario);
    }
}
