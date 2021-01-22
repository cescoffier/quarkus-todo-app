package io.quarkus.sample;

import java.util.List;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @OPTIONS
    public Response opt() {
        return Response.ok().build();
    }

    @GET
    public Uni<List<Todo>> getAll() {
        System.out.println(Thread.currentThread().getName());
        return Panache.withTransaction(
                () -> Todo.findAll(Sort.by("order")).list()
        );
    }

    @GET
    @Blocking
    @Path("/blocking")
    public List<Todo> getAllBlocking() {
        System.out.println(Thread.currentThread().getName());
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
        return Panache.withTransaction(
                () -> item.persist()
            ).replaceWith(
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
                () -> Todo.deleteCompleted()
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
                        .call(entity -> entity.delete())
        ).replaceWith(
                () -> Response.noContent().build()
        );
    }

}