# Cadastro, senhas e recovery

## Cadastro só por admin (`UsuarioService.java:40-58`, `AuthController.java:49-82`)

```java
usuario.setSenha(passwordEncoder.encode(senhaInicial));
usuario.setPerfil(perfil);
usuario.setAtivo(true);
```

- **O que faz:** cria a conta já ativa, com **um único perfil** escolhido pelo admin — nunca pelo próprio usuário (RN-18 do SIGEE).
- **O que é hash BCrypt:** função que transforma a senha num texto irreversível com "sal" embutido. O banco guarda só o hash (`Usuario.java:22`); na hora do login o Spring recalcula e compara. Roubou o banco? Não dá para voltar à senha original.
- **Validações:** senha inicial com mín. 8 caracteres; usuário e e-mail únicos (erro PT-BR amigável).

## Troca de senha (`UsuarioService.java:64-74`)

Confere a senha atual antes de gravar a nova (também com hash). Erro "Senha atual incorreta" vai para a tela e para a auditoria (`SENHA_TROCADA/FALHA`).

## Esqueci minha senha (`UsuarioService.java:76-104`)

```java
reset.setToken(UUID.randomUUID().toString().replace("-", ""));
reset.setExpiraEm(Instant.now().plus(Duration.ofMinutes(tokenMinutos)));
```

1. `POST /esqueci-senha` gera um token aleatório de uso único, válido por 60 min.
2. Resposta **sempre igual** ("se houver conta, o link foi gerado") — não revela se o e-mail existe.
3. Sem servidor de e-mail no projeto, o link aparece uma vez na tela (limitação documentada).
4. `POST /redefinir-senha/{token}` valida (inexistente/usado/expirado = erro), grava a nova senha e **destrava a conta** — faz sentido: quem tem o e-mail provou ser o dono.

## Bloqueio em números (`UsuarioService.java:106-126`, `Usuario.java:28-29`)

```java
private int falhasLogin = 0;
private Instant bloqueadoAte;
```

- Cada falha soma 1; na 5ª, `bloqueadoAte = agora + 15min` e o contador zera.
- `JpaUserDetailsService` lê `!usuario.bloqueado()` — conta travada nem chega a testar a senha.
- Login com sucesso limpa tudo (`registrarSucesso`).
