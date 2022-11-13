package hexlet.code.models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
@Setter
@Getter
public final class UrlCheck extends Model {
    @Id
    private long id;
    private final int statusCode;
    private final String title;
    private final String h1;
    @Lob
    private final String description;
    @ManyToOne(cascade = CascadeType.ALL)
    Url url;
    @WhenCreated
    private Instant createdAt;

    public UrlCheck(int statusCode, String title, String h1, String description, Url url) {
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.url = url;
    }
}
