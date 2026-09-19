# Login SIGEE em Java

Sistema modular de autenticação e autorização desenvolvido com Java 21, Spring Boot 3.4,
Spring Security, Thymeleaf e MongoDB. Usuários e sessões HTTP são persistidos no MongoDB.

## Funcionalidades

- login e logout por sessão, com CSRF habilitado;
- cadastro de usuários restrito ao perfil `ADMINISTRADOR`;
- perfis `ADMINISTRADOR`, `OPERADOR` e `PROFESSOR`;
- senhas protegidas com BCrypt;
- bloqueio temporário após cinco tentativas inválidas;
- recuperação de senha por link enviado por e-mail, com token de uso único;
- aceite versionado dos termos e registro de auditoria;
- templates Thymeleaf e fragmentos reutilizáveis.

## Estrutura

```text
src/main/java/com/pfc/
├── security/                 # handlers e interceptadores de segurança
└── thindesk/
    ├── controller/           # rotas MVC e API
    ├── dto/                  # validação das entradas dos formulários
    ├── entity/               # documentos persistidos no MongoDB
    ├── repository/           # acesso ao MongoDB
    └── service/              # regras de negócio

src/main/resources/
├── static/                   # CSS, JavaScript e imagens
└── templates/                # páginas e fragmentos Thymeleaf
```

As regras de autenticação ficam centralizadas no Spring Security. Controllers recebem e
validam os dados, services aplicam as regras e repositories cuidam da persistência. Essa
separação permite trocar textos, imagens e templates sem alterar a lógica de login.

## Execução com Docker

Pré-requisito: Docker com Compose.

```powershell
Copy-Item .env.example .env
# Defina uma senha forte em APP_ADMIN_PASSWORD no arquivo .env
docker compose up --build
```

- aplicação: `http://localhost:8080/login`
- caixa de e-mail local do Mailpit: `http://localhost:8025`
- MongoDB local: `localhost:27018`

O Dockerfile possui build em duas etapas e gera o JAR automaticamente. Não é necessário
ter Maven ou Java instalados na máquina para executar dessa forma.

## Execução local sem Docker

Pré-requisitos: Java 21 e um MongoDB acessível.

```powershell
$env:MONGODB_URI="mongodb://localhost:27017/loginsigee"
$env:MONGODB_DATABASE="loginsigee"
$env:APP_ADMIN_PASSWORD="defina-uma-senha-forte"
$env:SMTP_HOST="localhost"
$env:SMTP_PORT="1025"
$env:APP_BASE_URL="http://localhost:8080"
.\mvnw.cmd spring-boot:run
```

## MongoDB Atlas

1. Crie um cluster no Atlas e um usuário de banco com acesso ao database do sistema.
2. Autorize o IP da máquina ou do ambiente onde a aplicação será executada.
3. Copie a connection string do driver Java e defina as variáveis abaixo:

```env
MONGODB_URI=mongodb+srv://USUARIO:SENHA@CLUSTER.mongodb.net/loginsigee?retryWrites=true&w=majority
MONGODB_DATABASE=loginsigee
```

Caracteres especiais do usuário e da senha precisam estar codificados para URL. A URI deve
ficar somente no `.env` ou nas variáveis do ambiente de hospedagem; o `.env` está ignorado
pelo Git. O mesmo MongoDB armazena usuários, tokens, auditoria, dados funcionais e a coleção
`sessions` mantida pelo Spring Session.

## Configuração de e-mail

A recuperação não mostra o token na tela. O link é enviado pelo servidor SMTP configurado:

```env
SMTP_HOST=smtp.exemplo.com
SMTP_PORT=587
SMTP_USERNAME=usuario
SMTP_PASSWORD=senha
SMTP_AUTH=true
SMTP_STARTTLS=true
SMTP_FROM=no-reply@exemplo.com
APP_BASE_URL=https://sistema.exemplo.com
```

No Docker local, o Mailpit já recebe as mensagens sem credenciais.

## Testes

```powershell
.\mvnw.cmd test
```

Os testes cobrem validação do cadastro, hash de senha, duplicidade de usuário e geração de
tokens de recuperação. Para produção, mantenha `APP_ADMIN_PASSWORD`, credenciais do Atlas e
SMTP apenas no gerenciador de variáveis secretas da plataforma.
