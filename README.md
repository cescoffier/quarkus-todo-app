# TODO Applications with Quarkus

![GitHub Workflow Status](https://img.shields.io/github/workflow/status/cescoffier/quarkus-todo-app/Build)

## Database

Run:

```bash
docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 \
    --name postgres-quarkus-rest-http-crud -e POSTGRES_USER=restcrud \
    -e POSTGRES_PASSWORD=restcrud -e POSTGRES_DB=rest-crud \
    -p 5432:5432 postgres:13.1
```

## Imperative Application

```bash
cd quarkus-todo
mvn compile quarkus:dev
```

Open: http://localhost:8080/

## Reactive Application

This version uses Hibernate Reactive, RESTEasy Reactive and Mutiny.

```bash
cd quarkus-todo-reactive
mvn compile quarkus:dev
```

Open: http://localhost:8080/

