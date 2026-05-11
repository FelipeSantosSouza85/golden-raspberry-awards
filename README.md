# Golden Raspberry Awards API

API RESTful para consulta dos vencedores da categoria **Pior Filme** do *Golden Raspberry Awards*. O serviço lê um arquivo CSV de filmes na inicialização e expõe um endpoint que retorna os produtores com o **menor** e o **maior** intervalo entre duas vitórias consecutivas.

---

## Tecnologias utilizadas

- **Java 21**
- **Quarkus 3.35.2** (REST + Hibernate ORM Panache + SmallRye OpenAPI)
- **Banco H2** em memória (embarcado, sem instalação externa)
- **Maven** (Maven Wrapper já incluso — não é necessário instalar Maven)
- **JUnit 5 + REST Assured** para testes de integração

---

## Pré-requisitos

- **JDK 21** ou superior instalado e configurado em `JAVA_HOME`.

---

## Como executar a aplicação

### Modo desenvolvimento (com recarga automática)

Linux / macOS:

```bash
./mvnw quarkus:dev
```

Windows (PowerShell):

```powershell
.\mvnw.cmd quarkus:dev
```

### Empacotar e executar

```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

A aplicação sobe em `http://localhost:8080` com o caminho base `/golden-raspberry-awards/api`.

---

## Como executar os testes de integração

```bash
./mvnw test
```

Os testes sobem o contexto Quarkus completo, carregam o CSV em H2 em memória e fazem chamadas HTTP reais via REST Assured. Eles cobrem:

- Resposta no formato esperado pela especificação para o CSV padrão.
- Invariantes do contrato (consistência do `interval`, ordenação `previousWin < followingWin`, `min ≤ max`) — passam com qualquer CSV de entrada.
- Cálculo de intervalo quando a linha vencedora tem múltiplos produtores separados por `and`, usando uma situação já presente no CSV padrão.
- Situação de empate simultâneo em `min` e `max`, usando o CSV de teste `src/test/resources/Movielist-tie-min-max.csv` via `QuarkusTestProfile`.
- Situação em que o mesmo produtor vence duas vezes no mesmo ano, usando o CSV de teste `src/test/resources/Movielist-same-producer-same-year.csv` para garantir que `interval = 0` não seja retornado.

---

## Endpoint disponível

### `GET /v1/producers/award-intervals`

URL completa:

```http
GET http://localhost:8080/golden-raspberry-awards/api/v1/producers/award-intervals
```

Exemplo de chamada:

```bash
curl http://localhost:8080/golden-raspberry-awards/api/v1/producers/award-intervals
```

Exemplo de resposta:

```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

`min` e `max` são listas — quando há produtores empatados no menor (ou maior) intervalo, todos são retornados.

---

## Documentação OpenAPI / Swagger UI

- **Swagger UI**: <http://localhost:8080/golden-raspberry-awards/api/q/swagger-ui>

---

## Arquivo CSV de entrada

O arquivo é carregado do classpath em `src/main/resources/Movielist.csv`.

**Formato esperado:**

- Separador: `;` (ponto e vírgula)
- Codificação: UTF-8
- Cabeçalho: `year;title;studios;producers;winner`
- Coluna `winner`: `yes` para vencedor, vazio caso contrário
- Coluna `producers`: aceita múltiplos produtores na mesma linha, separados por `,` ou pela palavra ` and ` (sem distinção de maiúsculas/minúsculas)

**Para avaliar com outro conjunto de dados:** substitua o arquivo `src/main/resources/Movielist.csv` pelo CSV desejado (mantendo o mesmo cabeçalho e separador) e reempacote a aplicação com `./mvnw package`.

---

## Estrutura do projeto

```text
src/main/java/br/com/outsera/
├── api/              → Controladores REST, DTOs e mappers (camada de apresentação)
├── application/      → Serviços de negócio (carga e cálculo de intervalos)
├── domain/           → Modelo de domínio (records, sem dependências de framework)
└── infrastructure/   → Persistência (JPA/Panache), leitura de CSV, inicialização
src/test/resources/
├── Movielist-tie-min-max.csv → Massa isolada para validar empates em min e max
└── Movielist-same-producer-same-year.csv → Massa isolada para validar vitórias duplicadas no mesmo ano
```

---

## Configurações relevantes

Definidas em `src/main/resources/application.properties`:

| Propriedade                   | Valor padrão                                | Descrição                          |
| ----------------------------- | ------------------------------------------- | ---------------------------------- |
| `quarkus.http.port`           | `8080`                                      | Porta HTTP                         |
| `quarkus.http.root-path`      | `/golden-raspberry-awards/api`              | Prefixo de todos os endpoints      |
| `quarkus.datasource.jdbc.url` | `jdbc:h2:mem:movielist;DB_CLOSE_DELAY=-1`   | H2 em memória                      |
| `app.csv.file-name`           | `Movielist.csv`                             | Nome do arquivo CSV no classpath   |
