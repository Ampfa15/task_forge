# TaskForge - JWT-secured Tasks API

A production-ready Spring Boot 3 Java 17 API demonstrating:
- JWT auth (register/login)
- Tasks CRUD scoped to the authenticated user
- JPA/Hibernate with H2 (dev) and Postgres (prod)
- Dockerfile and docker-compose for one-command run

## Quickstart (Dev with H2)

```bash
cd taskforge
mvn -DskipTests spring-boot:run
```

App runs at `http://localhost:8081`.

### Try it

```bash
# Register
curl -s -X POST http://localhost:8081/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{"email":"dev@taskforge.dev","password":"secret123","displayName":"Dev"}'

# Login (if registering again)
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"dev@taskforge.dev","password":"secret123"}' | jq -r .token)

# List tasks
curl -s -H "Authorization: Bearer $TOKEN" http://localhost:8081/api/tasks

# Create task
curl -s -X POST http://localhost:8081/api/tasks \
  -H 'Content-Type: application/json' \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"title":"Readme Task","description":"Created from README"}'
```

## Production (Docker + Postgres)

```bash
cd taskforge
mvn -DskipTests package
docker compose up --build
```

- API: `http://localhost:8080`
- DB: Postgres exposed on `5433`

To override JWT secret in prod: set env `TASKFORGE_SECURITY_JWTSECRET` or mount an `application-prod.yml`.

## API

- `POST /api/auth/register` { email, password, displayName } -> { token }
- `POST /api/auth/login` { email, password } -> { token }
- `GET /api/tasks` -> list
- `POST /api/tasks` { title, description? } -> task
- `PATCH /api/tasks/{id}` { title?, description?, completed? } -> task
- `DELETE /api/tasks/{id}` -> 204

## Tech
- Spring Boot 3.3, Spring Security, JPA/Hibernate
- H2 (dev), Postgres (prod)
- Java 17

## License
MIT
