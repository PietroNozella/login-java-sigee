# Auditoria, perfis e 403

## O que é auditado (`AutenticacaoHandlers.java` + `AuthController.java` + `LegalController.java`)

| Ação | Quando |
|---|---|
| `LOGIN` SUCESSO/FALHA | Todo login |
| `CONTA_BLOQUEADA` | Na 5ª tentativa inválida |
| `LOGOUT` | Saída via POST |
| `ACESSO_NEGADO` | Tentativa de abrir página sem permissão |
| `USUARIO_CADASTRADO` | Cadastro por admin (sucesso e falha) |
| `SENHA_TROCADA` / `SENHA_REDEFINIDA` | Troca e recovery |
| `RECOVERY_SOLICITADO` | Pedido de link (sempre SUCESSO, resposta neutra) |
| `ACEITE_REGISTRADO` | Aceite dos documentos |

Cada registro guarda quando, quem, ação, resultado e IP (`AuditoriaService.java`).

## Tela de auditoria (`/auditoria`, `AuditoriaController.java`)

Somente leitura, só `ADMINISTRADOR` — regra dupla: rota no `SecurityConfig` + botão escondido no sidebar (`sec:authorize`). Igual ao SIGEE.

## Matriz de acesso (fale com esta tabela)

| Ação | Admin | Operador | Professor |
|---|---|---|---|
| Entrar/sair | Sim | Sim | Sim |
| Consultar chamados/clientes/horários | Sim | Sim | Sim |
| Criar/editar/cancelar chamados | Sim | Sim | **Não (403)** |
| Salvar/deletar horários | Sim | Sim | **Não** |
| Cadastrar usuários, ver auditoria | Sim | Não | Não |

## O 403 (`AutenticacaoHandlers.java:80-88`)

```java
auditoriaService.registrar(username, RegistroAuditoria.Acao.ACESSO_NEGADO, ...);
response.sendRedirect(request.getContextPath() + "/403");
```

Sem permissão → grava `ACESSO_NEGADO` e mostra a página `403.html` ("Você não tem permissão"). A decisão é sempre no servidor; esconder botão é só cortesia visual.
