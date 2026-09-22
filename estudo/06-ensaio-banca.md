# Ensaio de banca (30 segundos por resposta)

Para cada arquivo central: onde abrir, o que mostrar, o que falar. Treine sem ler.

## `SecurityConfig.java` — "Como o acesso é controlado?"

- Mostre as linhas de `requestMatchers`: público (`/login`, `/termos`...) vs. `hasRole("ADMINISTRADOR")` vs. leitura autenticada na API.
- Fale: "A decisão é sempre no servidor, de cima para baixo; esconder botão no sidebar é só cortesia visual."

## `AutenticacaoHandlers.java` — "O que acontece no login?"

- Mostre `loginSucesso` (zera falhas, audita, redireciona) e `loginFalha` (conta tentativa, na 5ª grava `CONTA_BLOQUEADA` e manda para `/login?bloqueado`).
- Fale: "Efeito colateral centralizado aqui; a regra de quantas tentativas está parametrizada em `application.properties`."

## `UsuarioService.java` — "Como funciona o recovery?"

- Mostre `solicitarRecovery` (retorna `null` para e-mail inexistente, persiste só o hash SHA-256) e `redefinirSenha` (rejeita usado/expirado, marca `usado=true`, destrava a conta).
- Fale: "Resposta neutra para não enumerar contas; quem tem o link do e-mail provou ser o dono, por isso destrava."

## Reserva (se perguntarem da base do professor)

- "O domínio de chamados veio da base thindesk adaptada; o pacote agora é `com.pfc.sigee` e a origem segue creditada no README. Todo o acesso foi escrito espelhando o SIGEE."
- Dívida assumida: `status` e horários como texto — decisão registrada em `05-decisoes.md`.
