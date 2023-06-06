package io.quarkus.sample;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

import java.util.List;

@Path("/api")
public class TodoResource {

    @OPTIONS
    public Response opt() {
        return Response.ok().build();
    }

    @GET
    public Uni<List<Todo>> getAll() {
        return Panache.withTransaction(
                () -> Todo.findAll(Sort.by("order")).list()
        );
    }

    @GET
    @Path("/blocking")
    public List<Todo> getAllBlocking() {
        return getAll()
                .await().indefinitely();
    }

    @GET
    @Path("/{id}")
    public Uni<Todo> getOne(@PathParam("id") Long id) {
        return Panache.withTransaction(
                () -> Todo.<Todo>findById(id)
                        .onItem().ifNull().failWith(() ->
                                new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND)
                        ));
    }

    @POST
    public Uni<Response> create(@Valid Todo item) {
        return Panache.withTransaction(item::persist)
                .replaceWith(
                        () -> Response.status(Status.CREATED).entity(item).build()
                );
    }

    @PATCH
    @Path("/{id}")
    public Uni<Todo> update(@Valid Todo todo, @PathParam("id") Long id) {
        return Panache.withTransaction(
                () -> Todo.<Todo>findById(id)
                        .onItem().transform(entity -> {
                            entity.id = id;
                            entity.completed = todo.completed;
                            entity.order = todo.order;
                            entity.title = todo.title;
                            entity.url = todo.url;
                            return entity;
                        })
        );
    }

    @DELETE
    public Uni<Response> deleteCompleted() {
        return Panache.withTransaction(
                Todo::deleteCompleted
        ).replaceWith(
                () -> Response.noContent().build()
        );
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteOne(@PathParam("id") Long id) {
        return Panache.withTransaction(
                () -> Todo.findById(id)
                        .onItem().ifNull().failWith(() ->
                                new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND)
                        )
                        .call(PanacheEntityBase::delete)
        ).replaceWith(
                () -> Response.noContent().build()
        );
    }

}