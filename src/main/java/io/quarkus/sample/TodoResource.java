package io.quarkus.sample;

import io.quarkus.panache.common.Sort;
import org.jboss.logging.Logger;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.List;


@Path("/api")
@Produces("application/json")
@Consumes("application/json")
public class TodoResource {

    private static final Logger LOGGER = Logger.getLogger(TodoResource.class);

    @OPTIONS
    public Response opt() {
        return Response.ok().build();
    }

    @GET
    public Response getAll() {
        final List<Todo> todos = Todo.findAllCacheable();
//        LOGGER.infof("%s | %s",
//            CacheUtil.showEntityCacheStats("getAll", Todo.class)
//            , CacheUtil.showQueryCacheStats()
//        );
        return Response.ok(todos)
            .header("Pod-Name", PodResource.getPodName())
            .build();
    }

    @GET
    @Path("/{id}")
    public Response getOne(@PathParam("id") Long id) {
        Todo entity = Todo.findById(id);
        if (entity == null) {
            throw new WebApplicationException("Todo with id of " + id + " does not exist.", Status.NOT_FOUND);
        }
        LOGGER.info(CacheUtil.showEntityCacheStats("getOne", Todo.class));
        return Response.ok(entity)
            .header("Pod-Name", PodResource.getPodName())
            .build();
    }

    @POST
    @Transactional
    public Response create(@Valid Todo item) {
        item.persist();
        return Response.status(Status.CREATED).entity(item).build();
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