package hexlet.code.models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public final class Url extends Model {
    @Id
    private long id;

    private String name;

    @WhenCreated
    private Instant createAt;

    public Url(String name) {
        this.name = name;
    }

    public Url() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Instant getCreateAt() {
        return createAt;
    }
}
