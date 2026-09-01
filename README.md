# Contact Management System

Cohort 9 — JAVA Fullstack (JAVA+ReactJS) assignment for Abdullah Adil.

A web app where users register, sign in, and manage their own contacts. Each
contact has a name, title, and any number of labelled email addresses and phone
numbers. Contacts are private to the user who created them.

## Tech stack

**Backend** — Java 21, Spring Boot 4.1, Spring Data JPA / Hibernate, Spring
Security with JWT, Bean Validation, Slf4j + Logback, Flyway, JUnit 5 + Mockito,
SQL Server (H2 for local development).

**Frontend** — React 19, Vite, React Router, axios.

**Tooling** — Maven wrapper, JaCoCo, SonarCloud, GitHub Actions, Docker Compose.

## Layout

```
backend/    Spring Boot API
frontend/   React single page app
docker-compose.yml   SQL Server for local development
```

## Running it

You need JDK 21+ and Node 20+. Both parts run at the same time, in two
terminals.

### Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Starts on http://localhost:8080 using an in-memory H2 database, so there is
nothing to install first. The data is wiped on every restart. The H2 console is
at http://localhost:8080/h2-console (JDBC URL `jdbc:h2:mem:contactsdb`, user
`sa`, no password).

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Starts on http://localhost:5173 and proxies `/api` to the backend, so there is
no CORS setup in development. If the backend is on a different port, set
`BACKEND_URL` before starting, for example `BACKEND_URL=http://localhost:8081`.

### Running against SQL Server

The `prod` profile targets SQL Server and Flyway creates the schema. Start the
database with Docker:

```bash
SA_PASSWORD='YourStrong@Passw0rd' docker compose up -d
```

Then run the backend with the credentials passed in (they have no defaults, so
nothing usable is committed to the repo):

```bash
cd backend && DB_USERNAME=sa DB_PASSWORD='YourStrong@Passw0rd' JWT_SECRET='a-long-random-secret-at-least-32-characters' ./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## Tests

```bash
cd backend && ./mvnw verify
```

47 tests covering the service, controller and repository layers, plus a
coverage report written to `backend/target/site/jacoco/`.

## API

All `/api/contacts` and `/api/users` endpoints need an
`Authorization: Bearer <token>` header, using the token returned by register or
login.

| Method | Path | Description |
| --- | --- | --- |
| POST | `/api/auth/register` | Register with an email or a phone number |
| POST | `/api/auth/login` | Sign in with either identifier |
| GET | `/api/users/me` | Details of the signed-in user |
| PUT | `/api/users/me/password` | Change password |
| GET | `/api/contacts` | List contacts, paginated (`page`, `size`, `search`) |
| POST | `/api/contacts` | Create a contact |
| GET | `/api/contacts/{id}` | Get one contact |
| PUT | `/api/contacts/{id}` | Update a contact |
| DELETE | `/api/contacts/{id}` | Delete a contact |

## Code quality

`mvnw verify` produces a JaCoCo coverage report, and GitHub Actions runs the
build, the tests, and the frontend lint on every push. SonarCloud analysis runs
too once these are set on the repository:

- secret `SONAR_TOKEN`
- variables `SONAR_ORGANIZATION` and `SONAR_PROJECT_KEY`
