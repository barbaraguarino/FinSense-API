# Backlog Sequencial

## Introdução

Este documento atua como o **Roteiro de Implementação** do projeto **FinSense**. Diferente de um backlog ágil tradicional que pode ser reordenado a cada Sprint, esta lista representa uma **sequência lógica de dependências**: a infraestrutura suporta a segurança, que suporta o negócio.

### Definição de Pronto (Global DoD)

Para que qualquer item abaixo seja marcado como `[x]`, ele deve cumprir:

- [ ]  O código compila sem erros e warnings.
- [ ]  Testes unitários cobrem as regras de negócio principais.
- [ ]  A funcionalidade foi testada via Postman/Swagger.
- [ ]  Não há segredos (senhas/chaves) commitados no código (uso de Variáveis de Ambiente).

## Pronto Para Execução

Estas tarefas já foram planejadas tecnicamente e devem ser executadas na ordem abaixo.

- [ ]  **[#001] Internacionalização (i18n)**
    
    **Motivação:** Preparar a API para responder em múltiplos idiomas desde o primeiro dia, evitando refatoração massiva de strings hardcoded no futuro.
    
    1. **Objetivo:** Configurar o Spring Boot para resolver mensagens baseadas no header `Accept-Language`.
    2. **Critérios de Aceitação:**
        - [ ]  A aplicação deve conter arquivos de propriedades: `messages.properties` (fallback), `messages_pt.properties`, `messages_en.properties`.
        - [ ]  Configurar um `LocaleResolver` ou interceptor para ler o header `Accept-Language` da requisição HTTP.
        - [ ]  Criar um endpoint de teste simples que retorna uma mensagem traduzida (ex: "Olá mundo" vs "Hello World").
        - [ ]  As mensagens de validação do Bean Validation (`@NotNull`, `@Email`) devem ser lidas desses arquivos.
- [ ]  **[#002] Tratamento Global de Erros**
    
    **Motivação:** Padronizar as respostas de erro da API para que o frontend (ou quem consome) sempre saiba o formato do JSON em caso de falha.
    
    1. **Objetivo:** Centralizar o tratamento de exceções usando `@ControllerAdvice`.
    2. **Critérios de Aceitação:**
        - [ ]  Implementar classe `GlobalExceptionHandler`.
        - [ ]  O formato de erro deve seguir a **RFC 7807 (Problem Details)** ou um padrão interno consistente contendo: `timestamp`, `status`, `error`, `message` (traduzida) e `path`.
        - [ ]  Capturar `MethodArgumentNotValidException` e retornar uma lista detalhada dos campos inválidos.
        - [ ]  Criar uma exceção base de negócio (ex: `BusinessRuleException`) para ser estendida por erros específicos.
- [ ]  **[#003] Cadastro de Usuário**
    
    **Como** visitante, **quero** criar minha conta informando e-mail, nome e senha, **para que** eu possa iniciar meu acesso ao FinSense.
    
    1. **Critério de Aceitação:** 
        - [ ]  Endpoint `POST /api/v1/auth/signup`.
        - [ ]  O corpo da requisição (`request body`) deve ser um DTO (`RegisterUserRequestDTO`) contendo os campos:
            1. `name` (String)
            2. `email` (String)
            3. `password` (String)
        - [ ]  O sistema deve validar o formato do e-mail e a força da senha (mínimo 10 caracteres, letras, números e caractere especial).
        - [ ]  A senha **deve** ser criptografada (BCrypt) antes de ser salva.
        - [ ]  O e-mail deve ser único no banco de dados (case-insensitive).
        - [ ]  O usuário é criado com status inicial `PENDING` (ou `DISABLED`).
        - [ ]  Neste momento, apenas logar no console que um e-mail deveria ser enviado (preparação para a issue #004).
    2. **Critérios de Aceitação:**
        - [ ]  Cadastro com dados válidos -> Retorna 201 Created.
        - [ ]  Cadastro com e-mail já existente -> Retorna 409 Conflict com mensagem traduzida.
        - [ ]  Cadastro com senha fraca -> Retorna 400 Bad Request com detalhes do erro.
- [ ]  **[#004]** **Integração AWS SES**
    
    **Motivação:** Permitir que o sistema envie e-mails reais para verificação de conta e notificações, substituindo logs de console.
    
    1. **Objetivo:** Configurar o cliente AWS SDK e um serviço de e-mail resiliente.
    2. **Critério de Testes**: 
        - [ ]  Adicionar dependência do AWS SDK v2 para SES.
        - [ ]  Configurar credenciais da AWS via Variáveis de Ambiente (nunca hardcoded).
        - [ ]  Implementar `EmailService` com método `sendEmail(to, subject, body)`.
        - [ ]  O envio deve ser assíncrono (`@Async`) para não bloquear a thread principal da requisição.
        - [ ]  (Opcional) Criar uma implementação "Mock" ou "Dev" que apenas loga o e-mail se o profile for `local`, para economizar cota da AWS durante desenvolvimento.
- [ ]  **[#005] Ativação de Conta**
    
    **Como** novo usuário, **quero** validar o token recebido no meu e-mail, **para que** minha conta seja ativada e eu possa fazer login.
    
    1. **Critério de Aceitação:** 
        - [ ]  Refatorar o fluxo de Cadastro (#003) para gerar um token aleatório (UUID ou numérico) e enviar via `EmailService` (#004).
        - [ ]  Criar endpoint `POST /api/v1/auth/activate` (ou GET se for link direto).
        - [ ]  Validar se o token existe e se não expirou (ex: validade de 24h).
        - [ ]  Se válido: Alterar status do usuário para `ACTIVE` e invalidar o token usado.
        - [ ]  Se inválido/expirado: Retornar 400 Bad Request ou 404 Not Found com mensagem explicativa.
    2. **Critério de Testes**: 
        - [ ]  Token válido dentro do prazo -> Conta ativa, retorna 200 OK.
        - [ ]  Token expirado -> Retorna erro informando expiração.
        - [ ]  Token inexistente -> Retorna erro.
- [ ]  **[#006] E-mail de Boas Vindas ao Sistema**
    
    **Como** usuário recém-ativado, **quero** receber um e-mail de confirmação, **para que** eu tenha certeza que meu registro foi concluído com sucesso.
    
    1. **Critério de Aceitação:** 
        - [ ]  O sistema deve disparar um evento (Event Listener) assim que a conta for ativada na issue #005.
        - [ ]  Enviar e-mail de "Bem-vindo ao FinSense".
        - [ ]  O template do e-mail pode ser texto simples por enquanto, focando na entrega.
    2. **Critério de Testes**: 
        - [ ]  Ativar conta -> Verificar nos logs/AWS se o segundo e-mail (boas vindas) foi disparado.
- [ ]  **[#007] Login Seguro com HttpOnly Cookies**
    
    **Como** usuário cadastrado, **quero** fazer login com minhas credenciais, **para que** o sistema mantenha minha sessão segura sem expor tokens no navegador.
    
    1. **Critério de Aceitação:** 
        - [ ]  Implementar `UserDetailsService` do Spring Security para buscar o usuário no banco.
        - [ ]  Endpoint `POST /api/v1/auth/login`.
        - [ ]  Validar credenciais (match da senha hash).
        - [ ]  Gerar Token (JWT ou Session ID opaco).
        - [ ]  **Importante:** O token **não** deve ser retornado no corpo do JSON. Ele deve ser setado num Cookie.
        - [ ]  Configuração do Cookie: `HttpOnly=true`, `Secure=true` (se prod), `SameSite=Strict`, `Path=/`.
    2. **Critério de Testes**: 
        - [ ]  Login com sucesso -> Resposta 200 OK e Header `Set-Cookie` presente.
        - [ ]  Login com senha errada -> Resposta 401 Unauthorized.
        - [ ]  Tentar acessar endpoint protegido sem o cookie -> Resposta 401/403.

## Próximos Passos

Aqui ficam as funcionalidades mapeadas para o futuro. Quando a seção “Execução Imediata” esvaziar, deve ser puxado itens daqui, detalhado os critérios técnicos e movido para cima.  

1. **Login com Google (OAuth2):** Permitir entrada sem senha. Se o e-mail não existir, criar conta automaticamente.
2. **Vincular Contas:** Usuário logado com senha pode "conectar" seu Google.
3. **Desvincular Contas:** Permitir remover o Google (apenas se tiver senha definida).
4. **Auditoria:** Salvar IP e User-Agent de todo login realizado.
