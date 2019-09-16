package io.quarkus.sample;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@Cacheable
@NamedQuery(
    name = Todo.QUERY_NAME,
    query = "SELECT t from Todo t ORDER BY t.order",
    hints = @QueryHint(name = "org.hibernate.cacheable", value = "true")
)
public class Todo extends PanacheEntity {

    static final String QUERY_NAME = "Todo.findAllCacheable";

    @NotBlank
    @Column(unique = true)
    public String title;

    public boolean completed;

    @Column(name = "ordering")
    public int order;

    public String url;

    public static List<Todo> findNotCompleted() {
        return list("completed", false);
    }

    public static List<Todo> findCompleted() {
        return list("completed", true);
    }

    public static long deleteCompleted() {
        return delete("completed", true);
    }

    public static List<Todo> findAllCacheable() {
        final EntityManager entityManager = Panache.getEntityManager();
        final Query namedQuery = entityManager.createNamedQuery(QUERY_NAME);
        return namedQuery.getResultList();
    }

}
