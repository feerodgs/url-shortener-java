# URL Shortener API

Uma API REST para encurtar URLs e acompanhar a quantidade de acessos de cada link.

O projeto foi construído com **Java 21**, **Spring Boot** e **Amazon DynamoDB**, com foco em praticar integração de aplicações backend com serviços de cloud.

> Atualmente, a API é executada localmente e utiliza o Amazon DynamoDB na AWS para persistir os links criados.  
> A próxima evolução planejada é realizar o deploy serverless usando AWS Lambda e API Gateway.

## Arquitetura

```text
Cliente HTTP (Postman)
        ↓
Spring Boot REST API
        ↓
Service Layer
        ↓
Repository Layer
        ↓
AWS SDK for Java v2
        ↓
Amazon DynamoDB
```

## Funcionalidades

- Criar links encurtados a partir de URLs longas.
- Gerar códigos curtos aleatórios com seis caracteres.
- Redirecionar para a URL original.
- Registrar e consultar a quantidade de acessos de cada link.
- Validar URLs recebidas pela API.
- Persistir links e métricas de acesso no Amazon DynamoDB.
- Usar incremento atômico no DynamoDB para o contador de cliques.

## Tecnologias

- Java 21
- Spring Boot 4
- Spring MVC
- Maven
- AWS SDK for Java v2
- Amazon DynamoDB
- Postman

## Estrutura do projeto

```text
src/main/java/com/feerodogs/urlshortener
├── config
│   └── DynamoDbConfig.java
├── controller
│   └── UrlController.java
├── domain
│   └── ShortenedUrl.java
├── dto
│   ├── request
│   │   └── CreateShortUrlRequest.java
│   └── response
│       ├── CreateShortUrlResponse.java
│       └── UrlStatisticsResponse.java
├── repository
│   ├── DynamoDbUrlRepository.java
│   └── UrlRepository.java
├── service
│   └── UrlShortenerService.java
└── util
    └── ShortCodeGenerator.java
```

## Endpoints

### Criar URL curta

```http
POST /api/urls
Content-Type: application/json
```

**Request body:**

```json
{
  "url": "[https://www.linkedin.com/in/feliperodrigues22/](https://www.linkedin.com/in/feliperodrigues22/)"
}
```

**Response — `201 Created`:**

```json
{
  "shortCode": "6Co7rX",
  "shortUrl": "http://localhost:8080/6Co7rX",
  "originalUrl": "[https://www.linkedin.com/in/feliperodrigues22/](https://www.linkedin.com/in/feliperodrigues22/)",
  "createdAt": "2026-09-16T22:32:45.424508900Z"
}
```

### Redirecionar para a URL original

```http
GET /{shortCode}
```

**Exemplo:**

```http
GET /6Co7rX
```

**Response — `302 Found`:**

```http
Location: [https://www.linkedin.com/in/feliperodrigues22/](https://www.linkedin.com/in/feliperodrigues22/)
```

A requisição incrementa o contador de acessos no DynamoDB.

### Consultar estatísticas

```http
GET /api/urls/{shortCode}
```

**Exemplo:**

```http
GET /api/urls/6Co7rX
```

**Response — `200 OK`:**

```json
{
  "shortCode": "6Co7rX",
  "originalUrl": "[https://www.linkedin.com/in/feliperodrigues22/](https://www.linkedin.com/in/feliperodrigues22/)",
  "createdAt": "2026-09-16T22:32:45.424508900Z",
  "clicks": 1
}
```

## Modelagem no DynamoDB

A tabela `url-shortener` utiliza `shortCode` como chave de partição.

| Atributo | Tipo | Descrição |
|---|---|---|
| `shortCode` | String | Código curto e chave primária do link |
| `originalUrl` | String | URL original para redirecionamento |
| `createdAt` | String | Data de criação em ISO-8601 |
| `clicks` | Number | Total de acessos do link |

O contador utiliza uma operação atômica do DynamoDB:

```text
ADD clicks :increment
```

Isso evita perda de atualizações quando mais de uma requisição acessa o mesmo link ao mesmo tempo.

## Executando localmente

### Pré-requisitos

- Java 21
- AWS CLI configurada com um usuário IAM que possua acesso ao DynamoDB
- Uma tabela DynamoDB chamada `url-shortener` na região `us-east-1`

### Configuração

O arquivo `src/main/resources/application.yaml` possui configurações não sensíveis:

```yaml
aws:
  region: us-east-1
  dynamodb:
    table-name: url-shortener
```

As credenciais AWS não ficam no projeto. Durante o desenvolvimento local, elas são resolvidas pelo `DefaultCredentialsProvider` do AWS SDK, utilizando o perfil configurado pela AWS CLI.

### Executar

No Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

A aplicação ficará disponível em:

```text
http://localhost:8080
```

### Exemplo com PowerShell

```powershell
Invoke-RestMethod `
  -Uri "http://localhost:8080/api/urls" `
  -Method Post `
  -ContentType "application/json" `
  -Body '{"url":"[https://www.linkedin.com/in/feliperodrigues22/](https://www.linkedin.com/in/feliperodrigues22/)"}'
```

## Testes manuais

O fluxo pode ser validado no Postman:

1. `POST /api/urls` para criar o link.
2. `GET /api/urls/{shortCode}` para conferir as estatísticas iniciais.
3. `GET /{shortCode}` para validar o redirecionamento.
4. `GET /api/urls/{shortCode}` novamente para confirmar que `clicks` aumentou.
5. Reiniciar a aplicação e repetir a consulta de estatísticas para validar a persistência no DynamoDB.

## Próximas evoluções

- [ ] Deploy serverless com AWS Lambda e API Gateway.
- [ ] Infrastructure as Code com AWS SAM.
- [ ] Tratamento global de exceções e respostas de erro padronizadas.
- [ ] Testes unitários e de integração.
- [ ] Rate limiting para reduzir abuso da API.
- [ ] Expiração de links com TTL no DynamoDB.
- [ ] Autenticação de usuários com Amazon Cognito.
- [ ] Dashboard de métricas com CloudWatch.

## Autor

Desenvolvido por [Felipe Rodrigues](https://www.linkedin.com/in/feliperodrigues22/).