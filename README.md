# OpenContact API

API RESTful para o sistema de gerenciamento de contatos OpenContact. Construída com as melhores práticas de mercado, utilizando Java, Spring Boot e Docker.

Este projeto serve como um backend robusto, escalável e de alta performance para gerenciar múltiplas agendas e seus respectivos contatos, com um ambiente de desenvolvimento 100% containerizado para facilitar a colaboração e o deploy.

## ✨ Features

- Gerenciamento completo (CRUD) para Agendas e Contatos.
- Listagem paginada e com filtros dinâmicos para contatos (por nome e telefone).
- Schema de banco de dados versionado com Liquibase.
- Seed de dados de exemplo para ambiente de desenvolvimento.
- Documentação da API interativa e gerada automaticamente com Swagger (OpenAPI).
- Endpoint de Health Check para monitoramento.
- Ambiente de desenvolvimento completo com Docker Compose (API + Banco de Dados).

## 🛠️ Stack de Tecnologias

- **Backend:**
    - [Java 21](https://www.oracle.com/java/)
    - [Spring Boot 3.5.5](https://spring.io/projects/spring-boot)
    - [Spring Data JPA (Hibernate)](https://spring.io/projects/spring-data-jpa)
    - [Spring Web](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
    - [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- **Banco de Dados:**
    - [PostgreSQL 16](https://www.postgresql.org/)
    - [Liquibase](https://www.liquibase.org/) (Para versionamento de schema e seed)
- **Documentação:**
    - [Springdoc OpenAPI (Swagger UI)](https://springdoc.org/)
- **DevOps:**
    - [Docker & Docker Compose](https://www.docker.com/)
- **Build:**
    - [Maven](https://maven.apache.org/)

## 🚀 Como Rodar o Projeto

Siga os passos abaixo para ter todo o ambiente (API e Banco de Dados) rodando na sua máquina local.

### Pré-requisitos

- [Git](https://git-scm.com/)
- [Docker](https://www.docker.com/products/docker-desktop/) e [Docker Compose](https://docs.docker.com/compose/)
- [Java 21](https://www.oracle.com/java/technologies/downloads/) (Opcional, apenas para rodar via IDE)
- [Maven](https://maven.apache.org/) (Opcional, apenas para rodar via IDE)

### Passos para Execução

1.  **Clone o repositório:**

    ```bash
    git clone https://github.com/RafaelQSantos-RQS/OpenContactApi
    cd OpenContactApi
    ```

2.  **Crie seu arquivo de ambiente:**
    Este projeto usa um arquivo `.env` para gerenciar as variáveis de ambiente. Copie o template para criar seu próprio arquivo de configuração.

    ```bash
    cp .env.template .env
    ```

    *(Nenhuma alteração é necessária no arquivo `.env` para rodar o ambiente de desenvolvimento padrão).*

3.  **Suba os contêineres com Docker Compose:**
    Este comando irá construir a imagem da sua API, baixar a imagem do PostgreSQL e iniciar ambos os serviços.

    ```bash
    docker compose up --build -d
    ```

    * `_--build_`: Força a reconstrução da imagem da API se houver mudanças no código.
    * `_-d_`: Roda os contêineres em modo "detached" (em background).

A API estará disponível em `http://localhost:8080`.

## 📖 Documentação da API

A API vem com uma documentação interativa completa gerada pelo Swagger UI. Após iniciar a aplicação, você pode acessá-la para ver todos os endpoints, modelos de dados e **testar a API diretamente pelo navegador**.

- **Swagger UI:** [http://localhost:8080/api/v1/swagger-ui.html](https://www.google.com/search?q=http://localhost:8080/api/v1/swagger-ui.html)

### Health Check

Para verificar a saúde da aplicação e de seus componentes (como a conexão com o banco de dados), acesse o endpoint do Actuator:

- **Health Check:** [http://localhost:8080/api/v1/management/health](https://www.google.com/search?q=http://localhost:8080/api/v1/management/health)

## 🗺️ Roadmap / Próximos Passos

Apesar de o MVP estar completo, existem várias melhorias que podem ser implementadas para tornar a API ainda mais robusta e pronta para produção:

- [ ] **Testes Automatizados:** Implementar testes unitários (JUnit/Mockito) para a camada de serviço e testes de integração para a camada de controller.
- [ ] **Segurança:** Adicionar autenticação e autorização usando Spring Security e JWT para proteger os endpoints.
- [ ] **Tratamento de Erros Avançado:** Criar um DTO de erro padrão para todas as respostas de erro da API.
- [ ] **Atualizações Parciais (PATCH):** Implementar o verbo HTTP `PATCH` para permitir a atualização de campos individuais de um recurso.
