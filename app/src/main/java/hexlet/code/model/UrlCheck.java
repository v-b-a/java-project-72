package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Setter
@Getter
public final class UrlCheck extends Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int statusCode;
    private String title;
    private String h1;
//    @Lob
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    private Url url;
    @WhenCreated
    private Instant createdAt;

    public UrlCheck(long id, int statusCode, String title, String h1, String description) {
        this.id = id;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }
}
