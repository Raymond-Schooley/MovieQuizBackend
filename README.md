# Movie Quiz - Backend

A Spring Boot 3 / Java 21 REST API backing the Movie Quiz app. It exposes a
small movie/actor catalog (`/api/v1/movies`, `/api/v1/actors`) and a quiz
endpoint that generates multiple-choice trivia questions from that catalog.

This repo is meant to be developed alongside a sibling `frontend` repo and a
small top-level orchestration folder that wires the two together with
nginx and MySQL - see the root `README.md` one level up for how to run the
whole stack. Everything below covers running just this service.

## Stack

- Java 21, Spring Boot 3.3
- Spring Web, Spring Data JPA, Bean Validation
- MySQL 8 (`com.mysql:mysql-connector-j`)
- Flyway for schema migrations (`src/main/resources/db/migration`)
- Lombok
- JUnit 5 + Mockito + AssertJ for tests

## Project layout

```
src/main/java/com/moviequiz/
  domain/       JPA entities (Movie, Actor, MovieActor)
  dto/          Request/response DTOs - controllers never expose entities
  repository/   Spring Data repositories, including the random-pick queries
                the quiz uses
  service/      Business logic (MovieService, ActorService, QuizService,
                QuizSessionStore)
  controller/   REST controllers
  exception/    Centralized error handling (@RestControllerAdvice)
src/main/resources/
  application.yml
  db/migration/ Flyway migrations (schema + seed data)
```

## Running it

### With Docker (recommended)

You don't need Java or Maven installed locally for this path - see the
root-level `docker-compose.yml` / `docker-compose.dev.yml` one directory up.
Those bring up MySQL and this service together, either as a production-like
build (`Dockerfile`) or as a live-reloading dev container (`Dockerfile.dev`).

### Locally, without Docker

Requirements: JDK 21, Maven 3.9+, and a MySQL 8 instance you point it at.

```bash
mvn spring-boot:run
```

By default it connects to `jdbc:mysql://localhost:3306/moviequiz` as
`moviequiz`/`moviequiz`. Override any of it with environment variables
(`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`,
`SERVER_PORT`) - see `application.yml`. Flyway creates the schema and loads
the sample dataset automatically on first startup.

### Tests

```bash
mvn test
```

`QuizServiceTest` covers the quiz generation/answer logic against mocked
repositories, so it doesn't need a database.

> **Note on this scaffold:** the sandbox this project was generated in
> couldn't reach Maven Central to actually compile it (its outbound network
> is locked down to a small allowlist). The code follows standard,
> well-worn Spring Boot 3 patterns, but run `mvn -q -DskipTests package` (or
> the Docker build) as your first step after cloning to confirm it builds
> clean in an environment with normal internet access, and let me know if
> anything doesn't compile so it can be fixed.

## API overview

| Method | Path                          | Description                              |
|--------|-------------------------------|-------------------------------------------|
| GET    | `/api/v1/movies`              | List movies (`?title=` to search)         |
| GET    | `/api/v1/movies/{id}`         | Get one movie, with cast                  |
| POST   | `/api/v1/movies`               | Create a movie                            |
| PUT    | `/api/v1/movies/{id}`         | Update a movie                            |
| DELETE | `/api/v1/movies/{id}`         | Delete a movie                            |
| GET    | `/api/v1/actors`               | List actors (`?name=` to search)          |
| GET    | `/api/v1/actors/{id}`         | Get one actor                             |
| POST   | `/api/v1/actors`               | Create an actor                           |
| PUT    | `/api/v1/actors/{id}`         | Update an actor                           |
| DELETE | `/api/v1/actors/{id}`         | Delete an actor                           |
| GET    | `/api/v1/quiz/questions/next` | Get a new multiple-choice question (`?minYear=&maxYear=&minVotes=`) |
| POST   | `/api/v1/quiz/answers`         | Submit `{questionId, selectedOptionId}`   |

### How the quiz endpoint works

`GET /quiz/questions/next` returns a question and a shuffled list of 4
options, each with an opaque id (`"1"`-`"4"`) - the correct answer's text
is never sent to the client. The server remembers the right answer for that
`questionId` in memory for 5 minutes. `POST /quiz/answers` looks it up,
tells you whether you were right, and invalidates the question so it can't
be answered twice. See `QuizSessionStore` for why this is in-memory rather
than persisted, and what to swap in if you ever run more than one backend
instance behind a load balancer.

## Sample data

`V2__seed_data.sql` loads 14 well-known movies and their top-billed cast so
the quiz has something to work with out of the box. `Old/MovieConnect` (in
the parent folder) has the original version of this project, including
scripts to build a *much* larger dataset from the full IMDB non-commercial
datasets - worth a look if you want to grow past the seed data.
