# API de Chamados do IFRN

API REST para registrar e acompanhar chamados de suporte do IFRN. O projeto é um monólito modular em camadas: cada módulo reúne controller, service, repository, entidades e DTOs; regras de negócio ficam exclusivamente nos services.

## Executar

Com Docker instalado, execute:

```bash
docker compose up --build
```

A API ficará disponível em `http://localhost:8080`. O compose cria e mantém os dados do PostgreSQL no volume `postgres_data`. Para encerrar, use `docker compose down`; acrescente `-v` apenas se desejar descartar também os dados do banco.

## Rotas

| Recurso | Rotas |
| --- | --- |
| Setores | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` em `/setores` |
| Categorias | `POST`, `GET`, `GET /{id}`, `PUT /{id}`, `DELETE /{id}` em `/categorias`; `GET /setores/{id}/categorias` |
| Usuários | `POST`, `GET`, `GET /{id}`, `PUT /{id}` em `/usuarios` |
| Chamados | `POST`, `GET`, `GET /{id}`, `PATCH /{id}/assumir`, `PATCH /{id}/resolver`, `PATCH /{id}/cancelar` em `/chamados`; `POST /chamados/{id}/interacoes` |

Listagens aceitam `page`, `size` (máximo 50) e `sort`, por exemplo `?page=0&size=10&sort=dataAbertura,desc` para chamados. As rotas também aceitam os filtros pedidos no enunciado: `nome`, `setorId`, `tipo`, `status`, `prioridade`, `categoriaId`, `solicitanteId`, `tecnicoId`, `dataInicio` e `dataFim`, conforme o recurso.

Os dados iniciais incluem dois setores, quatro categorias, dois solicitantes e dois técnicos. Veja todas as chamadas no arquivo [`chamados.http`](chamados.http).

## Exemplo

```http
POST /chamados
Content-Type: application/json

{
  "title": "Projetor sem imagem",
  "description": "O projetor da sala 12 liga, mas não exibe imagem.",
  "location": "Sala 12",
  "priority": "ALTA",
  "requesterId": 1,
  "categoryId": 1
}
```

## Migrations

O esquema é criado por migrations Flyway em `src/main/resources/db/migration`; o Hibernate fica em modo `validate` e nunca cria ou altera tabelas. O Flyway registra cada versão e seu checksum em `flyway_schema_history`. Se o conteúdo de uma migration já aplicada for alterado, a validação falha porque o checksum deixa de corresponder. Por isso, uma correção entra sempre em uma migration nova. `ddl-auto=update` não é usado em produção porque mudanças implícitas no modelo podem alterar o banco sem revisão, histórico ou previsibilidade.
