package io.quarkus.sample;

import io.quarkus.panache.common.Sort;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;

@Path("/api")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @OPTIONS
    public Response opt() {
        return Response.ok().build();
    }

    @GET
    public Multi<Todo> getAll() {
        System.out.println(Thread.currentThread().getName());
        return Todo.streamAll(Sort.by("order"));
    }

    @GET
    @Blocking
    @Path("/blocking")
    public List<Todo> getAllBlocking() {
        System.out.println(Thread.currentThread().getName());
        return getAll()
                .collectItems().asList()
                .await().indefinitely();
    }

    @GET
    @Path("/{id}")
    public Uni<Todo> getOne(@PathParam("id") Long id) {
        return Todo.findById(id)
                .onItem().ifNull().failWith(() ->
                        new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND)
                )
                .onItem().castTo(Todo.class);
    }

    @POST
    public Uni<Response> create(@Valid Todo item) {
        return item.persistAndFlush()
                .onItem().transform(x -> Response.status(Status.CREATED).entity(item).build());
    }

    @PATCH
    @Path("/{id}")
    public Uni<Todo> update(@Valid Todo todo, @PathParam("id") Long id) {
        return Todo.<Todo>findById(id)
                .onItem().transform(entity -> {
                    entity.id = id;
                    entity.completed = todo.completed;
                    entity.order = todo.order;
                    entity.title = todo.title;
                    entity.url = todo.url;
                    return entity;
                })
                .onItem().call(t -> t.flush());
    }

    @DELETE
    public Uni<Response> deleteCompleted() {
        return Todo.deleteCompleted()
                .onItem().transform(x -> Response.noContent().build());
    }

    @DELETE
    @Path("/{id}")
    public Uni<Response> deleteOne(@PathParam("id") Long id) {
        return Todo.findById(id)
                .onItem().ifNull().failWith(() ->
                        new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND)
                )
                .call(entity -> entity.delete())
                .chain(entity -> entity.flush())
                .onItem().transform(x -> Response.noContent().build());
    }

}