# O caminho de um login, trecho a trecho

## 1. A tela (`templates/login.html:53-64`)

```html
<form th:action="@{/login}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
    ...
    <input type="text" ... name="username" ...>
    <input type="password" ... name="password" ...>
    <button type="submit">Entrar</button>
</form>
```

- **O que faz:** envia usuário + senha para `/login`.
- **Por que o campo escondido:** é o token CSRF — prova que o form veio do nosso site, não de um site falso. Sem ele, o servidor rejeita (erro 403).
- **No SIGEE:** igual, o `{% csrf_token %}` do Django.

## 2. As regras (`SecurityConfig.java:24-33`)

```java
.requestMatchers("/login", "/esqueci-senha", "/redefinir-senha/**",
        "/termos", "/privacidade", "/403", ...).permitAll()
.requestMatchers("/usuarios/**", "/auditoria/**").hasRole("ADMINISTRADOR")
```

- **O que faz:** lista o que é público (login, termos...) e o que exige perfil (cadastro e auditoria, só admin).
- **Conceito Java:** `@Bean SecurityFilterChain` = a "corrente de filtros" que toda requisição atravessa; cada `requestMatchers` é uma regra lida de cima para baixo.

## 3. Buscando o usuário (`JpaUserDetailsService.java:21-30`)

```java
public UserDetails loadUserByUsername(String username) ... {
    Usuario usuario = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado."));
    return new User(usuario.getUsername(), usuario.getSenha(), usuario.isAtivo(),
            true, true, !usuario.bloqueado(),
            List.of(new SimpleGrantedAuthority(usuario.getPerfil().autoridade())));
}
```

- **O que faz:** carrega a conta do Mongo e entrega ao Spring: nome, hash da senha, se está ativa, se está **desbloqueada** (`!usuario.bloqueado()`) e o perfil como permissão (`ROLE_ADMINISTRADOR` etc.).
- **Por que importa:** o Spring compara sozinho a senha digitada com o hash. Erro aqui = mensagem neutra "Usuário ou senha inválidos" (nunca diz qual dos dois errou).

## 4. Sucesso e falha (`AutenticacaoHandlers.java:32-65`)

Sucesso: zera o contador de falhas, grava auditoria `LOGIN/SUCESSO`, manda para `/`.

```java
boolean bloqueouAgora = usuarioService.registrarFalha(username);
```

- **O que faz na falha:** conta +1 erro; na 5ª tentativa, trava a conta por 15 min e manda para `/login?bloqueado`. Tudo vai para a auditoria (`LOGIN/FALHA` ou `CONTA_BLOQUEADA`).
- **No SIGEE:** não existia bloqueio — é novidade nossa.

## 5. O desvio do aceite (`AceiteInterceptor.java:29-41`)

```java
.map(u -> u.aceiteEmDia(termosVersao, privacidadeVersao))
...
response.sendRedirect(request.getContextPath() + "/aceite");
```

- **O que faz:** a cada página interna, confere se o usuário aceitou a versão vigente dos documentos. Se não, barra e manda para `/aceite`.
- **Conceito Java:** interceptor = "pedágio" que inspeciona a requisição antes do controller. Equivale ao middleware do SIGEE.
