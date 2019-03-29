# TODO Application with Quarkus


## Prerequisites

* You need the _master_ branch of Quarkus (or Quarkus 0.13.0+)


## Database

Run:

```bash
docker run --ulimit memlock=-1:-1 -it --rm=true --memory-swappiness=0 \
    --name postgres-quarkus-rest-http-crud -e POSTGRES_USER=restcrud \
    -e POSTGRES_PASSWORD=restcrud -e POSTGRES_DB=rest-crud \
    -p 5432:5432 postgres:10.5
```

## Application

```bash
mvn compile quarkus:dev
```

Open: http://localhost:8080/todo.html

