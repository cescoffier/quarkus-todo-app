package io.quarkus.sample;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Todo extends PanacheEntity {

    @NotBlank
    @Column(unique = true)
    public String title;

    public boolean completed;

    @Column(name = "ordering")
    public int order;

    public String url;

    public static Uni<List<Todo>> findNotCompleted() {
        return list("completed", false);
    }

    public static Uni<List<Todo>> findCompleted() {
        return list("completed", true);
    }

    public static Uni<Long> deleteCompleted() {
        return delete("completed", true);
    }

}
