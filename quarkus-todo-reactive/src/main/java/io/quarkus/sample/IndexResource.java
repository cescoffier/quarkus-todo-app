package io.quarkus.sample;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/")
public class IndexResource {

    @GET
    public Response redirect() {
        URI redirect = UriBuilder.fromUri("todo.html").build();
        return Response.temporaryRedirect(redirect).build();
    }
}