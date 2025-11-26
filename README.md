# FinSense - Intelligent Financial Tracker

O **FinSense** é uma API de gerenciamento financeiro pessoal focada em segurança, privacidade e inteligência comportamental. O objetivo deste projeto é demonstrar a aplicação de padrões avançados de Engenharia de Software, arquitetura limpa e integração com serviços AWS.

Diferente de gerenciadores comuns, o FinSense utiliza Inteligência Artificial para aprender os padrões de consumo do usuário de forma implícita, eliminando a necessidade de microgerenciamento manual.

## Tecnologias e Arquitetura

Este projeto segue os princípios **SOLID** utilizando uma **Clean Architecture Pragmática**, garantindo que as regras de negócio independam de frameworks e banco de dados.

- **Linguagem:** Java 21 (LTS) - *Focado em compatibilidade nativa com AWS.*
- **Framework:** Spring Boot 4.0.0
- **Banco de Dados:** PostgreSQL (AWS RDS Free Tier).
- **Migrações:** Flyway.
- **Segurança:** Spring Security, OAuth2 Client, JWT (HttpOnly Cookies), TOTP (2FA).
- **Cloud (AWS):** Elastic Beanstalk (Deploy), S3 (Armazenamento), SES (E-mails transacionais).
- **IA & Analytics:** Algoritmos estatísticos para previsão financeira e arquitetura pronta para integração com LLMs (Gemini/OpenAI/HuggingFace).
- **Documentação:** SpringDoc OpenAPI (Swagger UI).
- **Mapeamento:** DTOs com conversores manuais (sem bibliotecas de terceiros para mapeamento).

## Funcionalidades Chave

### Segurança & Auditoria (Security First)

- **Autenticação Robusta:** Login via E-mail/Senha e OAuth2 (Google/Facebook) com gestão de sessão via Cookies `HttpOnly` + `Secure`.
- **Múltiplos Fatores (2FA):** Integração com apps autenticadores (TOTP) para proteção crítica.
- **Auditoria de Acesso:** Histórico detalhado de logins contendo IP, geolocalização aproximada, User-Agent (dispositivo/navegador) e data.
- **Gestão de Sessões:** Visualização de todos os dispositivos conectados com opção de "Deslogar remotamente".
- **Proteção de Dados:** Criptografia de dados sensíveis em repouso (Data at Rest) no banco de dados.

### Privacidade e Controle (Compliance)

- **Direito ao Esquecimento:** Exclusão de conta com "Soft Delete" imediato e agendamento automático de exclusão física (Hard Delete) após 30 dias.
- **Central de Privacidade:** Gerenciamento granular de consentimento para notificações e uso de dados.

### Inteligência Financeira (Core)

- **Smart Categorization:** Algoritmos estatísticos que aprendem os padrões de consumo do usuário para sugerir categorias e prever despesas futuras (sem necessidade de IA Generativa custosa).
- **Insights Naturais:** (Integração Opcional) Camada de tradução de dados para Linguagem Natural, oferecendo feedback humanizado sobre a saúde financeira.

### Smart Scan (Automação)

- **Leitura de Nota Fiscal (QR Code):** Importação automática de itens de compra através da leitura do QR Code de notas fiscais (NFC-e).*
- **OCR de Comprovantes:** Digitalização de recibos físicos via foto, extraindo data e valor automaticamente para aprovação do usuário (Integração AWS Textract).*

*(Funcionalidades marcadas com * estão em estágio experimental de desenvolvimento)*

## Estrutura do Diretórios

A estrutura reflete a separação de responsabilidades:

```latex
com.finsense.api
├── application             # REGRA DE APLICAÇÃO (Orquestração)
│   ├── dto                 # Objetos de Transferência (Input/Output dos serviços)
│   ├── service             # Casos de Uso (ex: UserRegistrationService)
│   └── exception           # Exceções de Regra de Negócio (ex: UserAlreadyExistsException)
│
├── domain                  # REGRA DE NEGÓCIO (O Coração)
│   ├── model               # Suas Entidades puras (pode ter anotações JPA por pragmatismo)
│   │   ├── user            # Agrupado por contexto (User, Profile)
│   │   └── finance         # Agrupado por contexto (Transaction, Wallet)
│   └── repository          # Interfaces dos Repositórios (Contratos)
│
├── infrastructure          # O MUNDO EXTERNO (Frameworks, Drivers)
│   ├── config              # Configurações (Beans, Swagger, i18n)
│   ├── persistence         # Implementação dos Repositórios (Spring Data JPA)
│   ├── security            # Auth, Tokens, OAuth2
│   └── integration         # Clientes externos (AWS SES, S3, Textract, ReceitaWS)
│
└── presentation            # A PORTA DE ENTRADA (Interface Web)
    ├── controller          # Recebe HTTP e devolve HTTP
    ├── mapper              # Converte DTO <-> Entidade (Manual)
    └── advice              # Tratamento Global de Erros (GlobalExceptionHandler)
```

## Como executar

*Instruções de configuração de ambiente e variáveis da AWS serão adicionadas em breve.*

---

*Desenvolvido por **Barbara Duarte**.*
