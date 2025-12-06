# Backlog Sequencial

## Introdução

Este documento atua como o **Roteiro de Implementação** do projeto **FinSense**. Diferente de um backlog ágil tradicional que pode ser reordenado a cada Sprint, esta lista representa uma **sequência lógica de dependências**: a infraestrutura suporta a segurança, que suporta o negócio.

### Definição de Pronto (Global DoD)

Para que qualquer item abaixo seja marcado como `[x]`, ele deve cumprir:

- [ ]  O código compila sem erros e warnings.
- [ ]  Testes unitários cobrem as regras de negócio principais.
- [ ]  A funcionalidade foi testada via Postman/Swagger.
- [ ]  Documentação (Swagger/README) atualizada.
- [ ]  Pull Request aprovado e mergeado na branch `dev`.

## Guia de Classificação e Etiquetas

Para manter o fluxo de trabalho do **FinSense** organizado e previsível, utilizamos um sistema tridimensional de classificação para cada Issue no GitHub. Isso nos permite filtrar o backlog rapidamente por urgência (Prioridade), esforço (Tamanho) e domínio técnico (Tag).

### Prioridade (Priority)

Define a **ordem de execução**. Responde à pergunta: "O quão urgente é isso para o negócio?"

| **Etiqueta** | **Significado** | **Quando usar?** |
| --- | --- | --- |
| **P0 (Critical)** | **Bloqueador / Emergência** | O sistema não roda, o build quebrou, há uma falha de segurança grave ou bloqueia todos os outros devs. **Ação:** Para tudo e resolve. |
| **P1 (High)** | **MVP Core** | Funcionalidades essenciais. Sem isso, o produto não cumpre seu propósito (ex: Login, Cadastro, Transação). Foco das primeiras Sprints. |
| **P2 (Medium)** | **Melhoria / Secundário** | Funcionalidades importantes, mas o produto "vive" sem elas por um tempo (ex: Dashboards, Relatórios, Filtros avançados). |
| **P3 (Low)** | **Cosmético / Futuro** | Ajustes visuais, refatorações não críticas ou ideias "nice-to-have" para versões 2.0. |

### Tamanho (Size)

*Define a **estimativa de esforço**. Responde à pergunta: "Quanto tempo/complexidade isso leva?"Nota: As horas são aproximadas e servem para evitar que peguemos mais trabalho do que conseguimos entregar.*

- **XS (Extra Small):** *< 8 horas (1 dia)*.
    - Ajustes de configuração (`application.properties`), correção de typo, bug simples, criar um DTO isolado.
- **S (Small):** *~ 16 horas (2 dias)*.
    - Tarefa de rotina. Criar um endpoint simples sem muita lógica de negócio, criar uma migração de banco simples.
- **M (Medium):** *~ 3 dias*.
    - Uma feature padrão. Envolve criar Controller, Service, Repository, Testes Unitários e tratar erros. (Ex: CRUD de Categorias).
- **L (Large):** *~ 4 a 5 dias (1 semana)*.
    - Funcionalidade complexa ou com integrações externas. (Ex: Configurar Security com JWT, Integração com Gateway de Pagamento).
- **XL (Extra Large):** *Bloqueante*.
    - **Regra:** Se uma tarefa for classificada como XL, ela é **grande demais**. Ela deve ser quebrada em 2 ou mais tarefas (ex: separar o Back-end do Front-end, ou separar a configuração da implementação).

### Tags de Domínio

Define a **natureza técnica** da tarefa. Responde à pergunta: "Onde no sistema eu vou mexer?"

- **`bug`:** Correção de falhas. Algo funcionava e parou, ou não funciona conforme a especificação.
- **`core`:** O coração do sistema. Lógica de domínio, regras de negócio financeiras, cálculos e arquitetura base.
- **`feat`:** (Feature) Nova funcionalidade perceptível para o usuário final. Gera valor direto.
- **`docs`:** Documentação. Atualizar o README, Swagger, diagramas ou Wiki.
- **`infra`:** DevOps. Docker, CI/CD (GitHub Actions), AWS, Banco de Dados, Logs.
- **`sec`:** Segurança. Autenticação, Autorização, Criptografia, Tokens, Sanitização de dados.
- **`question`:** Investigação (Spike). Quando não sabemos como fazer e precisamos de um tempo para estudar antes de codar.

## Pronto Para Execução

Estas tarefas já foram planejadas tecnicamente e devem ser executadas na ordem abaixo.

- [x]  **#1 Internacionalização (i18n)**
    
    **Motivação:** Preparar a API para responder em múltiplos idiomas desde o primeiro dia, evitando refatoração massiva de strings hardcoded no futuro.
    
    1. **Objetivo:** Configurar o Spring Boot para resolver mensagens baseadas no header `Accept-Language`.
    2. **Prioridade:** P1
    3. **Tamanho: S**
    4. **Tag:** `core` 
    5. **Critérios de Aceitação:**
        - [x]  A aplicação deve conter arquivos de propriedades: `messages.properties` (fallback), `messages_pt_BR.properties`, `messages_en_US.properties`.
        - [x]  Configurar um `LocaleResolver` ou interceptor para ler o header `Accept-Language` da requisição HTTP.
        - [x]  Criar um endpoint de teste simples que retorna uma mensagem traduzida (ex: "Olá mundo" vs "Hello World").
        - [x]  As mensagens de validação do Bean Validation (`@NotNull`, `@Email`) devem ser lidas desses arquivos.
- [x]  **#2 Tratamento Global de Erros**
    
    **Motivação:** Padronizar as respostas de erro da API para que o frontend (ou quem consome) sempre saiba o formato do JSON em caso de falha.
    
    1. **Objetivo:** Centralizar o tratamento de exceções usando `@ControllerAdvice`.
    2. **Prioridade:** P1
    3. **Tamanho: S**
    4. **Tag:** `core`
    5. **Dependências**:
        - [x]  #1: As mensagens de erro devem usar o sistema de i18n
    6. **Critérios de Aceitação:**
        - [x]  Implementar classe `GlobalExceptionHandler`.
        - [x]  O formato de erro deve seguir a **RFC 7807 (Problem Details)** ou um padrão interno consistente contendo: `timestamp`, `status`, `error`, `message` (traduzida) e `path`.
        - [x]  Capturar `MethodArgumentNotValidException` e retornar uma lista detalhada dos campos inválidos.
        - [x]  Criar uma exceção base de negócio (ex: `BusinessRuleException`) para ser estendida por erros específicos.
- [ ]  **#3 Cadastro de Usuário**
    
    **Como** visitante, **quero** criar minha conta informando e-mail, nome e senha, **para que** eu possa iniciar meu acesso ao FinSense.
    
    1. **Prioridade:**  P1
    2. **Tamanho: M**
    3. **Tag:** `feat`
    4. **Dependências**:
        - [x]  #2: As validações de formulário precisam do Exception Handler para retornar JSON bonito
    5. **Critério de Aceitação:** 
        - [ ]  Endpoint `POST /api/v1/auth/signup`.
        - [x]  O corpo da requisição (`request body`) deve ser um DTO (`RegisterUserRequestDTO`) contendo os campos:
            1. `name` (String)
            2. `email` (String)
            3. `password` (String)
        - [x]  O sistema deve validar o formato do e-mail e a força da senha (mínimo 10 caracteres, letras, números e caractere especial).
        - [x]  A senha **deve** ser criptografada (BCrypt) antes de ser salva.
        - [x]  O e-mail deve ser único no banco de dados (case-insensitive).
        - [x]  O usuário é criado com status inicial `PENDING` (ou `DISABLED`).
        - [x]  Neste momento, apenas logar no console que um e-mail deveria ser enviado (preparação para a issue #004).
    6. **Critérios de Aceitação:**
        - [x]  Cadastro com dados válidos -> Retorna 201 Created.
        - [x]  Cadastro com e-mail já existente -> Retorna 409 Conflict com mensagem traduzida.
        - [ ]  Cadastro com senha fraca -> Retorna 400 Bad Request com detalhes do erro.
- [ ]  **#4** **Integração AWS SES**
    
    **Motivação:** Permitir que o sistema envie e-mails reais para verificação de conta e notificações, substituindo logs de console.
    
    1. **Objetivo:** Configurar o cliente AWS SDK e um serviço de e-mail resiliente.
    2. **Prioridade:** P1
    3. **Tamanho: S**
    4. **Tag:** `infra`
    5. **Critério de Aceitação**: 
        - [ ]  Adicionar dependência do AWS SDK v2 para SES.
        - [ ]  Configurar credenciais da AWS via Variáveis de Ambiente (nunca hardcoded).
        - [ ]  Implementar `EmailService` com método `sendEmail(to, subject, body)`.
        - [ ]  O envio deve ser assíncrono (`@Async`) para não bloquear a thread principal da requisição.
        - [ ]  (Opcional) Criar uma implementação "Mock" ou "Dev" que apenas loga o e-mail se o profile for `local`, para economizar cota da AWS durante desenvolvimento.
- [ ]  **#5 Ativação de Conta**
    
    **Como** novo usuário, **quero** validar o token recebido no meu e-mail, **para que** minha conta seja ativada e eu possa fazer login.
    
    1. **Prioridade:** P1
    2. **Tamanho: M**
    3. **Tag:** `sec`
    4. **Dependências**:
        - [ ]  #3: Precisa ter o usuário cadastrado
        - [ ]  #4: Precisa do serviço de e-mail para enviar o token
    5. **Critério de Aceitação:** 
        - [ ]  Refatorar o fluxo de Cadastro (#003) para gerar um token aleatório (UUID ou numérico) e enviar via `EmailService` (#004).
        - [ ]  Criar endpoint `POST /api/v1/auth/activate` (ou GET se for link direto).
        - [ ]  Validar se o token existe e se não expirou (ex: validade de 24h).
        - [ ]  Se válido: Alterar status do usuário para `ACTIVE` e invalidar o token usado.
        - [ ]  Se inválido/expirado: Retornar 400 Bad Request ou 404 Not Found com mensagem explicativa.
    6. **Critério de Testes**: 
        - [ ]  Token válido dentro do prazo -> Conta ativa, retorna 200 OK.
        - [ ]  Token expirado -> Retorna erro informando expiração.
        - [ ]  Token inexistente -> Retorna erro.
- [ ]  **#6 E-mail de Boas Vindas ao Sistema**
    
    **Como** usuário recém-ativado, **quero** receber um e-mail de confirmação, **para que** eu tenha certeza que meu registro foi concluído com sucesso.
    
    1. **Prioridade:** P2
    2. **Tamanho: XS**
    3. **Tag:** `feat`
    4. **Dependências**:
        - [ ]  #5: Disparado após o sucesso da ativação
    5. **Critério de Aceitação:** 
        - [ ]  O sistema deve disparar um evento (Event Listener) assim que a conta for ativada na issue #005.
        - [ ]  Enviar e-mail de "Bem-vindo ao FinSense".
        - [ ]  O template do e-mail pode ser texto simples por enquanto, focando na entrega.
    6. **Critério de Testes**: 
        - [ ]  Ativar conta -> Verificar nos logs/AWS se o segundo e-mail (boas vindas) foi disparado.
- [ ]  **#7 Login Seguro com HttpOnly Cookies**
    
    **Como** usuário cadastrado, **quero** fazer login com minhas credenciais, **para que** o sistema mantenha minha sessão segura sem expor tokens no navegador.
    
    1. **Prioridade:** P0
    2. **Tamanho:** L
    3. **Tag:** `sec`
    4. **Dependências**:
        - [ ]  #3: Precisa de usuário no banco para logar
        - [ ]  #2: Erros de autenticação 401/403 precisam seguir o padrão JSON.
    5. **Critério de Aceitação:** 
        - [ ]  Implementar `UserDetailsService` do Spring Security para buscar o usuário no banco.
        - [ ]  Endpoint `POST /api/v1/auth/login`.
        - [ ]  Validar credenciais (match da senha hash).
        - [ ]  Gerar Token (JWT ou Session ID opaco).
        - [ ]  **Importante:** O token **não** deve ser retornado no corpo do JSON. Ele deve ser setado num Cookie.
        - [ ]  Configuração do Cookie: `HttpOnly=true`, `Secure=true` (se prod), `SameSite=Strict`, `Path=/`.
    6. **Critério de Testes**: 
        - [ ]  Login com sucesso -> Resposta 200 OK e Header `Set-Cookie` presente.
        - [ ]  Login com senha errada -> Resposta 401 Unauthorized.
        - [ ]  Tentar acessar endpoint protegido sem o cookie -> Resposta 401/403.

## Próximos Passos

Aqui ficam as funcionalidades mapeadas para o futuro. Quando a seção “Execução Imediata” esvaziar, deve ser puxado itens daqui, detalhado os critérios técnicos e movido para cima.  

1. **Login com Google (OAuth2):** Permitir entrada sem senha. Se o e-mail não existir, criar conta automaticamente.
2. **Vincular Contas:** Usuário logado com senha pode "conectar" seu Google.
3. **Desvincular Contas:** Permitir remover o Google (apenas se tiver senha definida).
4. **Auditoria:** Salvar IP e User-Agent de todo login realizado.
