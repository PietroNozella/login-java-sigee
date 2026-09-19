package com.pfc.thindesk.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.pfc.thindesk.entity.Perfil;
import com.pfc.thindesk.entity.PasswordResetToken;
import com.pfc.thindesk.entity.Usuario;
import com.pfc.thindesk.repository.PasswordResetTokenRepository;
import com.pfc.thindesk.repository.UsuarioRepository;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    private UsuarioService usuarioService;

    @BeforeEach
    void configurar() {
        usuarioService = new UsuarioService();
        ReflectionTestUtils.setField(usuarioService, "usuarioRepository", usuarioRepository);
        ReflectionTestUtils.setField(usuarioService, "tokenRepository", tokenRepository);
        ReflectionTestUtils.setField(usuarioService, "passwordEncoder", new BCryptPasswordEncoder(4));
        ReflectionTestUtils.setField(usuarioService, "tokenMinutos", 60L);
        ReflectionTestUtils.setField(usuarioService, "maxTentativas", 5);
        ReflectionTestUtils.setField(usuarioService, "bloqueioMinutos", 15L);
    }

    @Test
    void deveCadastrarUsuarioComSenhaCriptografada() {
        when(usuarioRepository.findByUsername("maria")).thenReturn(Optional.empty());
        when(usuarioRepository.findByEmail("maria@escola.test")).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Usuario salvo = usuarioService.cadastrar(
                "Maria", "Silva", "maria@escola.test", "maria", "senha-segura", Perfil.PROFESSOR);

        assertThat(salvo.getSenha()).isNotEqualTo("senha-segura");
        assertThat(new BCryptPasswordEncoder().matches("senha-segura", salvo.getSenha())).isTrue();
        assertThat(salvo.getPerfil()).isEqualTo(Perfil.PROFESSOR);
    }

    @Test
    void deveRecusarUsernameDuplicado() {
        when(usuarioRepository.findByUsername("maria")).thenReturn(Optional.of(new Usuario()));

        assertThatThrownBy(() -> usuarioService.cadastrar(
                "Maria", "Silva", "maria@escola.test", "maria", "senha-segura", Perfil.PROFESSOR))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Nome de usuário já existe.");

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void recoveryNaoDeveCriarTokenParaEmailInexistente() {
        when(usuarioRepository.findByEmail("ausente@escola.test")).thenReturn(Optional.empty());

        assertThat(usuarioService.solicitarRecovery("ausente@escola.test")).isNull();
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void recoveryDeveCriarTokenUnicoComExpiracao() {
        Usuario usuario = new Usuario();
        usuario.setId("usuario-1");
        when(usuarioRepository.findByEmail("maria@escola.test")).thenReturn(Optional.of(usuario));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String tokenOriginal = usuarioService.solicitarRecovery("maria@escola.test");

        ArgumentCaptor<PasswordResetToken> tokenSalvo = ArgumentCaptor.forClass(PasswordResetToken.class);
        assertThat(tokenOriginal).hasSize(32);
        verify(tokenRepository).save(tokenSalvo.capture());
        assertThat(tokenSalvo.getValue().getToken()).hasSize(64).isNotEqualTo(tokenOriginal);
        assertThat(tokenSalvo.getValue().getUsuarioId()).isEqualTo("usuario-1");
        assertThat(tokenSalvo.getValue().getExpiraEm()).isNotNull();
    }

    @Test
    void deveBuscarRecoveryPeloHashEMarcarTokenComoUsado() {
        PasswordResetToken reset = new PasswordResetToken();
        reset.setUsuarioId("usuario-1");
        reset.setExpiraEm(Instant.now().plusSeconds(300));
        Usuario usuario = new Usuario();
        usuario.setId("usuario-1");

        when(tokenRepository.findByToken(any(String.class))).thenReturn(Optional.of(reset));
        when(usuarioRepository.findById("usuario-1")).thenReturn(Optional.of(usuario));

        usuarioService.redefinirSenha("token-recebido-por-email", "nova-senha-segura");

        verify(tokenRepository).findByToken(org.mockito.ArgumentMatchers.argThat(
                hash -> hash.length() == 64 && !hash.equals("token-recebido-por-email")));
        assertThat(reset.isUsado()).isTrue();
        assertThat(new BCryptPasswordEncoder().matches("nova-senha-segura", usuario.getSenha())).isTrue();
    }
}
