# login-sigee-java

Login completo em Java (Spring Boot 3.4 + MongoDB + BCrypt) espelhando o login do
SIGEE (Django), construído sobre a base `thindesk` do professor, adaptada.

## Stack

Java 21, Spring Boot (Web, Data MongoDB, Security, Thymeleaf, Validation),
MongoDB 7, BCrypt, Thymeleaf + Bootstrap 5.

## Subir local

```powershell
Copy-Item .env.example .env
# edite o .env e troque APP_ADMIN_PASSWORD
docker compose up --build
```

App em `http://localhost:8080/login` (conta seed do `.env`).

Sem Docker, com Mongo local na porta 27018:

```powershell
$env:MONGODB_URI="mongodb://localhost:27018/loginsigee"
$env:APP_ADMIN_PASSWORD="troque-aqui"
.\mvnw.cmd spring-boot:run
```

## Fluxos

- Login/logout por sessão, mensagem de erro neutra, CSRF ligado, logout só via POST
- Perfis `ADMINISTRADOR`, `OPERADOR`, `PROFESSOR`; sem cadastro público (só admin cadastra em `/usuarios/novo`)
- Bloqueio após 5 tentativas inválidas (15 min); destrava ao redefinir a senha
- Esqueci minha senha com token de uso único (60 min); sem SMTP, o link aparece uma vez na tela
- Aceite versionado de Termo de Uso + Privacidade obrigatório após o login
- Auditoria somente-leitura em `/auditoria` (só admin)
- Domínio helpdesk mínimo do professor: chamados, clientes, horários (`/chamados`, `/clientes`, `/ajustes-horarios`)
