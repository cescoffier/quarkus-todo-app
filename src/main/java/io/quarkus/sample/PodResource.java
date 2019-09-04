package io.quarkus.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/pod")
@Produces("text/plain")
public class PodResource {

    @GET
    @Path("/name")
    public String getName() {
        final String podName = System.getenv().get("MY_POD_NAME");
        return podName == null ? "localhost" : podName;
    }

}
