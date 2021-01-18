package io.quarkus.sample;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
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

    public static Multi<Todo> findNotCompleted() {
        return stream("completed", false);
    }

    public static Multi<Todo> findCompleted() {
        return stream("completed", true);
    }

    public static Uni<Long> deleteCompleted() {
        return delete("completed", true);
    }

}
