package io.quarkus.sample;

import io.quarkus.panache.common.Sort;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.transaction.NotSupportedException;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.List;


@Path("/api")
@Produces("application/json")
@Consumes("application/json")
public class TodoResource {

    @OPTIONS
    @NonBlocking
    public Response opt() {
        return Response.ok().build();
    }

    @GET
    @RunOnVirtualThread
    public List<Todo> getAll() {
        return Todo.listAll(
                Sort.by("order")
        );
    }

    @GET
    @Path("/{id}")
    @RunOnVirtualThread
    public Todo getOne(@PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new
                    WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
        return entity;
    }


    @Inject TransactionManager tx;

    @POST
//    @Transactional - we cannot combine @Transactional and @RunOnVirtualThread, it's disabled in Quarkus
    @RunOnVirtualThread
    public Response create(@Valid Todo item) throws SystemException, NotSupportedException {
        tx.begin();
        try {
            item.persist();
            tx.commit();
            return Response.status(Status.CREATED).entity(item).build();
        } catch (Exception e) {
            tx.rollback();
            return Response.serverError().build();
        }

    }

    @PATCH
    @Path("/{id}")
    @Transactional
    public Response update(@Valid Todo todo, @PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        entity.id = id;
        entity.completed = todo.completed;
        entity.order = todo.order;
        entity.title = todo.title;
        entity.url = todo.url;
        return Response.ok(entity).build();
    }

    @DELETE
    @Transactional
    public Response deleteCompleted() {
        Todo.deleteCompleted();
        return Response.noContent().build();
    }

    @DELETE
    @Transactional
    @Path("/{id}")
    public Response deleteOne(@PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
        entity.delete();
        return Response.noContent().build();
    }

}