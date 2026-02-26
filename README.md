# Product Management — Microsserviço de Gerenciamento de Produtos

Microsserviço desenvolvido com **Spring Boot** responsável pelo gerenciamento de produtos no inventário. Consome uma API externa de catálogo de produtos (`catalog-api`) via **OpenFeign** e persiste os dados localmente em banco de dados H2 em memória. O projeto segue o **padrão de camadas** (Controller → Service → Repository), garantindo separação de responsabilidades e facilidade de testes.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.3 |
| Spring Cloud OpenFeign | 5.0.1 |
| Spring Data JPA | — |
| H2 Database | — |
| Lombok | — |
| JUnit 5 + Mockito | — |
| PIT Mutation Testing | 1.17.3 |

---

## Arquitetura em Camadas

O projeto segue o padrão de camadas.

```
Controller  →  Service  →  Repository
                  ↕
            CatalogClient (Feign)
```

| Camada | Classe | Responsabilidade |
|---|---|---|
| **Controller** | `ProductController` | Recebe as requisições HTTP e delega para a Service |
| **Service** | `ProductService` | Contém a lógica de negócio e integra com o catálogo externo |
| **Repository** | `ProductRepository` | Acesso ao banco de dados via Spring Data JPA |
| **DTO** | `ProductDTO` | Objeto de transferência de dados entre as camadas e a API externa |
| **Entity** | `Product` | Entidade JPA mapeada no banco de dados |
| **Client** | `CatalogClient` | Interface Feign para consumo da API de catálogo externa |

---

## Integração com a Catalog API

O microsserviço consome uma API externa de catálogo rodando em `http://localhost:9999` através do cliente Feign `CatalogClient`.

| Método | Endpoint externo | Uso interno |
|---|---|---|
| `GET` | `/products` | Lista todos os produtos do catálogo |
| `GET` | `/products/{id}` | Busca produto por ID para atualização de estoque |
| `POST` | `/products` | Registra um novo produto no catálogo |

Ao cadastrar um produto neste microsserviço, os dados básicos (nome, descrição, preço) são primeiro enviados ao catálogo externo. O retorno do catálogo é então combinado com a quantidade de estoque informada pelo cliente para persistir o produto localmente.

---

## Endpoints da API

**Base URL:** `/productInventory/productManagement/v1/products`

---

### `GET /`

Lista todos os produtos cadastrados no inventário local.

- **Resposta:** `200 OK` com array de `ProductDTO`

**Exemplo de resposta:**
```json
[
  {
    "id": 1,
    "name": "Notebook",
    "description": "Gaming notebook",
    "price": 4999.99,
    "stock": 10,
    "status": true
  }
]
```

---

### `POST /`

Cadastra um novo produto. Os dados de nome, descrição e preço são enviados ao catálogo externo. O produto é salvo localmente com a quantidade de estoque informada. O campo `status` é definido automaticamente como `true` se `stock > 0`, ou `false` caso contrário.

- **Body (JSON):**
```json
{
  "name": "Notebook",
  "description": "Gaming notebook",
  "price": 4999.99,
  "stock": 10
}
```
- **Resposta:** `201 Created`

---

### `PUT /inventory/{id}?stock={quantidade}`

Atualiza a quantidade de estoque de um produto existente. Busca os dados atuais no catálogo externo pelo ID e salva localmente com o novo valor de estoque.

- **Path variable:** `id` — ID do produto
- **Query param:** `stock` — nova quantidade em estoque
- **Resposta:** `200 OK`

**Exemplo:**
```
PUT /productInventory/productManagement/v1/products/inventory/1?stock=25
```

---

## Modelo de Dados

### Product (Entidade JPA)

| Campo | Tipo | Coluna no BD | Descrição |
|---|---|---|---|
| `id` | `Long` | `id` | Identificador auto-gerado |
| `name` | `String` | `name` | Nome do produto |
| `description` | `String` | `description` | Descrição do produto |
| `price` | `Double` | `price` | Preço unitário |
| `stock` | `int` | `stock_quantity` | Quantidade em estoque |
| `status` | `boolean` | `is_active` | `true` se stock > 0, `false` caso contrário |

---

## Configuração

### `src/main/resources/application.properties`

```properties
spring.application.name=productmanagement

spring.datasource.url=jdbc:h2:mem:products;NON_KEYWORDS=STATUS
spring.datasource.username=****
spring.datasource.password=****

spring.jpa.hibernate.ddl-auto=create-drop

spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

O banco H2 é **em memória** — os dados são resetados a cada reinício da aplicação.

O console H2 está disponível em: `http://localhost:8080/h2-console`

---

## Executando o Projeto

```bash
./mvnw spring-boot:run
```

---

## Testes

O projeto possui **testes unitários** e suporte a **testes de mutação** com PIT.

---

### Testes Unitários

#### `ProductServiceTest` — Mockito puro (`@ExtendWith(MockitoExtension.class)`)

Testa a lógica de negócio da `ProductService` de forma isolada, sem subir o contexto Spring. As dependências `ProductRepository` e `CatalogClient` são mockadas com Mockito.

---

#### `ProductControllerTest` — MockMvc com contexto Spring (`@SpringBootTest`)

Testa os endpoints HTTP da `ProductController` utilizando `MockMvc`. A `ProductService` é mockada com `@MockitoBean`.

---

### Rodando os Testes Unitários

**Via terminal:**
```bash
./mvnw test
```

**Via IntelliJ:**
Clique no ícone **▶** verde ao lado de qualquer classe de teste ou método `@Test`. Não é necessário criar um perfil de execução adicional — o IntelliJ detecta JUnit 5 automaticamente.

> Não é necessário criar um `application.properties` separado para testes. Os testes de `ProductServiceTest` usam Mockito puro, sem subir o contexto Spring. O `ProductControllerTest` reutiliza o `application.properties` de `main`.

---

### Testes de Mutação (PIT)

O PIT está configurado no `pom.xml` com alvo em `com.ale.productmanagement.Service.*`. Ele gera mutações no código da `ProductService` (ex.: troca `> 0` por `>= 0`, remove chamadas de método, inverte condicionais) e verifica se os testes unitários existentes detectam essas mudanças.

**Via terminal:**
```bash
./mvnw org.pitest:pitest-maven:mutationCoverage
```

**Via IntelliJ (Maven tool window):**
`Maven` → `Plugins` → `pitest` → `pitest:mutationCoverage` → **▶ Run**

O relatório HTML é gerado em:
```
target/pit-reports/index.html
```
Abra o arquivo no navegador para visualizar o score de mutação detalhado por classe e método.

---

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/com/ale/productmanagement/
│   │   ├── ProductmanagementApplication.java   # Classe principal (@EnableFeignClients)
│   │   ├── CatalogClient.java                  # Cliente Feign para a Catalog API
│   │   ├── Controller/
│   │   │   └── ProductController.java          # Endpoints REST
│   │   ├── Service/
│   │   │   └── ProductService.java             # Lógica de negócio
│   │   ├── Repository/
│   │   │   └── ProductRepository.java          # Acesso ao banco de dados
│   │   ├── Entity/
│   │   │   └── Product.java                    # Entidade JPA
│   │   └── DTO/
│   │       └── ProductDTO.java                 # Objeto de transferência de dados
│   └── resources/
│       └── application.properties
└── test/
    └── java/com/ale/productmanagement/
        ├── ProductmanagementApplicationTests.java
        ├── Controller/
        │   └── ProductControllerTest.java
        └── Service/
            └── ProductServiceTest.java
```

