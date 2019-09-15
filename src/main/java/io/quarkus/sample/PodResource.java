package io.quarkus.sample;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.Objects;

@Path("/pod")
@Produces("text/plain")
public class PodResource {

    @GET
    @Path("/name")
    public String getName() {
        return getPodName();
    }

    static String getPodName() {
        final String podName = System.getenv().get("MY_POD_NAME");
        return Objects.isNull(podName) ? "localhost" : podName;
    }

}
