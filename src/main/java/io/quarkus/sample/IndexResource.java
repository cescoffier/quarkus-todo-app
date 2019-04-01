package io.quarkus.sample;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class IndexResource {

    @GET
    public void redirect() throws URISyntaxException {
        Response.temporaryRedirect(new URI("todo.html")); 
    }
}