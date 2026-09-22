# Decisões de projeto (ADR curto)

Registro das escolhas que a banca mais pergunta. Formato: contexto → decisão → alternativa descartada.

## 1. Pacote `com.pfc.sigee` em vez de `com.pfc.thindesk`

- **Contexto:** o domínio de chamados veio da base do professor (thindesk), mas o sistema de acesso é nosso, espelhado no SIGEE.
- **Decisão:** todo o código vive em `com.pfc.sigee`; a origem da base segue creditada no README e em `00-visao-geral.md`.
- **Descartado:** manter `thindesk` no pacote — o nome alheio no artefato final sugere colagem, não adaptação.

## 2. Sessões no MongoDB em vez de JWT

- **Contexto:** o enunciado exige usuários e sessões no MongoDB Atlas.
- **Decisão:** Spring Session com coleção `sessions` (`spring.session.store-type=mongodb`). Sessão no servidor permite revogar na hora (bloqueio, troca de perfil) e sobrevive a reinícios.
- **Descartado:** JWT stateless — exigiria lista de revogação paralela para o bloqueio imediato, complexidade sem ganho no escopo.

## 3. Bloqueio: 5 tentativas, 15 minutos

- **Contexto:** proteger o login contra força bruta sem punir o usuário legítimo que erra a senha.
- **Decisão:** `app.login.max-tentativas=5`, `app.login.bloqueio-minutos=15`, parametrizáveis via properties. Redefinir a senha destrava (quem tem o e-mail provou ser o dono).
- **Descartado:** bloqueio permanente (exigiria desbloqueio manual pelo admin) e captcha (dependência externa fora do escopo).

## 4. Recovery com resposta neutra

- **Contexto:** o endpoint `/esqueci-senha` recebe um e-mail e precisa não revelar se a conta existe (enumeração de usuários).
- **Decisão:** mensagem sempre igual + `solicitarRecovery` retorna `null` para e-mail inexistente; o banco guarda só o hash SHA-256 do token, de uso único e 60 min.
- **Descartado:** exibir o token na tela (vazava o segredo no front) e mensagem de erro distinta (vazava existência da conta).

## 5. `status` e horários como texto (dívida consciente)

- **Contexto:** `Chamado.status` e os campos de `HorarioAtendimento` vieram como `String` da base adaptada.
- **Decisão:** mantido como está para não quebrar os templates existentes; registrado aqui como pendência do PFC (migrar para enum/validação).
- **Descartado:** refatorar agora — risco de quebrar telas fora do escopo da atividade de login.

## 6. Tema por CSS + `APP_TEMA_NOME` (sem framework de temas)

- **Contexto:** o enunciado pede layouts configuráveis para temas futuros do PFC.
- **Decisão:** `layout.html` aplica `tema-<nome>` no `<body>` (via `TemaControllerAdvice`) e carrega `tema-<nome>.css` com variáveis `--tema-*`. Tema novo = copiar um CSS + uma variável de ambiente, sem tocar em Java.
- **Descartado:** biblioteca de temas ou `ThemeResolver` — camadas a mais para um requisito que se resolve com convenção de arquivos.
