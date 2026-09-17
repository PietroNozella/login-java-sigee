# 10 perguntas prováveis da banca (com respostas curtas)

1. **Por que BCrypt e não "criptografar" a senha?**
   Criptografia é reversível (tem chave para voltar); hash não. BCrypt ainda embaralha com "sal", então senhas iguais geram hashes diferentes. Vazou o banco, a senha continua protegida.

2. **Por que não há "criar conta" na tela de login?**
   RN-18 do SIGEE: sem cadastro público. Contas são criadas por um admin com perfil atribuído; a primeira vem do seed (`AdminSeed`).

3. **Como o token de recovery expira?**
   Guarda `expiraEm = agora + 60min`; na redefinição, `reset.isUsado() || reset.expirado()` rejeita. Uso único: depois de usar, marca `usado=true`.

4. **O que acontece após 5 senhas erradas?**
   `bloqueadoAte = agora + 15min`; o Spring barra antes de testar a senha (`LockedException` → `/login?bloqueado`). Redefinir a senha destrava.

5. **Por que a mensagem de erro é genérica?**
   Dizer "usuário não existe" permite enumerar contas. Mensagem neutra + recovery neutro não vazam informação.

6. **Para que serve o CSRF?**
   Impede que outro site envie forms em nome do usuário logado. Todo POST carrega o token; sem ele, 403.

7. **O que garante que só admin cadastra?**
   Duas camadas: rota `/usuarios/**` exige `ROLE_ADMINISTRADOR` no servidor + botão escondido no sidebar. Burlar a tela não adianta.

8. **Como o aceite é cobrado de novo se o texto mudar?**
   O interceptor compara `aceiteTermosVersao/aceitePrivacidadeVersao` do usuário com `app.legal.*-versao`. Subiu a versão, o aceite antigo "vence" e o desvio volta.

9. **De onde veio cada parte?**
   Domínio de chamados = base do professor adaptada (bugs de rota corrigidos, CRUD completado). Todo o resto de acesso = espelho do login do SIGEE, traduzido de stack.

10. **O que ficou de fora e por quê?**
    Envio de e-mail (sem SMTP; link exibido na tela), testes automatizados, MFA. Decisão de escopo de PFC, tudo registrado no README e na análise do projeto.
