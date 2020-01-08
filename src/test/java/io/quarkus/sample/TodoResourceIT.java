package io.quarkus.sample;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import io.quarkus.test.junit.NativeImageTest;
import io.restassured.common.mapper.TypeRef;
import org.apache.http.HttpStatus;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.RestAssured.get;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;

@Testcontainers
@NativeImageTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TodoResourceIT {

    @Container
    private static final PostgreSQLContainer DATABASE = new PostgreSQLContainer<>("postgres:10.5")
            .withDatabaseName("rest-crud")
            .withUsername("restcrud")
            .withPassword("restcrud")
            .withExposedPorts(5432)
            .withCreateContainerCmdModifier(cmd ->
                    cmd
                            .withHostName("localhost")
                            .withPortBindings(new PortBinding(Ports.Binding.bindPort(5499), new ExposedPort(5432)))
            );

    @Test
    @Order(1)
    void testInitialItems() {
        // No data inserted.
        List<Todo> todos = get("/api").then()
                .statusCode(HttpStatus.SC_OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(getTodoTypeRef());
        assertEquals(0, todos.size());
    }

    @Test
    @Order(2)
    void testAddingAnItem() {
        Todo todo = new Todo();
        todo.title = "testing the application";
        given()
                .body(todo)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .post("/api")
                .then()
                .statusCode(HttpStatus.SC_CREATED)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body("title", is(todo.title))
                .body("completed", is(false))
                .body("id", is(1));

        List<Todo> todos = get("/api").then()
                .statusCode(HttpStatus.SC_OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(getTodoTypeRef());
        assertEquals(1, todos.size());
    }

    @Test
    @Order(3)
    void testUpdatingAnItem() {
        Todo todo = new Todo();
        todo.title = "testing the application (updated)";
        todo.completed = true;
        given()
                .body(todo)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .pathParam("id", 1)
                .when()
                .patch("/api/{id}")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .body("title", is(todo.title))
                .body("completed", is(true))
                .body("id", is(1));
    }

    @Test
    @Order(4)
    void testDeletingAnItem() {
        given()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .pathParam("id", 1)
                .when()
                .delete("/api/{id}")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);

        List<Todo> todos = get("/api").then()
                .statusCode(HttpStatus.SC_OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .extract().body().as(getTodoTypeRef());
        assertEquals(0, todos.size());
    }

    @NotNull
    private TypeRef<List<Todo>> getTodoTypeRef() {
        return new TypeRef<List<Todo>>() {
            // Kept empty on purpose
        };
    }

}
