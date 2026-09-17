# Visão geral — login-sigee-java (cola de apresentação)

Sistema de helpdesk com **login completo**, em Java 21 + Spring Boot 3.4 + MongoDB + BCrypt.
O login espelha o do SIGEE (Django); o domínio de chamados vem da base do professor (thindesk), adaptada.

## O caminho de um usuário (fale nesta ordem)

1. Abre `/login`, digita usuário e senha.
2. Sistema valida, abre sessão e manda para `/`.
3. Sem aceite vigente dos documentos → desvio para `/aceite`.
4. Com aceite → home, com menus conforme o perfil.

## Os 3 perfis (iguais aos grupos do SIGEE)

| Perfil | Pode |
|---|---|
| `ADMINISTRADOR` | Tudo: cadastra usuários, vê auditoria, escreve em chamados |
| `OPERADOR` | Consulta tudo, escreve em chamados, registra movimentações |
| `PROFESSOR` | Consulta (chamados só leitura) |

Sem cadastro público: contas nascem pelas mãos de um admin (`/usuarios/novo`).
A primeira conta vem do `.env` via seed (`AdminSeed.java:43`).

## Onde mora cada coisa

| Assunto | Arquivo |
|---|---|
| Regras de quem entra onde | `src/main/java/com/pfc/SecurityConfig.java` |
| Busca do usuário + trava de bloqueio | `.../service/JpaUserDetailsService.java` |
| O que acontece no login/logout/erro | `.../security/AutenticacaoHandlers.java` |
| Desvio para o aceite | `.../security/AceiteInterceptor.java` |
| Cadastro, senhas, recovery, bloqueio | `.../service/UsuarioService.java` |
| Telas de login/cadastro/recovery | `AuthController.java` + `templates/` |
| Termos, privacidade, aceite | `LegalController.java` |
| Auditoria | `AuditoriaService.java` + `/auditoria` |
| Conta no banco | `entity/Usuario.java` (coleção `usuarios`) |

## Frases prontas para a banca

- "A base do professor deu o domínio de chamados; o modelo de acesso veio do nosso SIGEE."
- "Senha nunca é guardada em texto puro: só o hash BCrypt."
- "Recovery e bloqueio por tentativas são novidades que nem o SIGEE tinha."
