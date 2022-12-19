package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
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

    public UrlCheck(int statusCode, String title1, String h11, String description1, Url url1) {
        this.statusCode = statusCode;
        this.title = title1;
        this.h1 = h11;
        this.description = description1;
        this.url = url1;
    }
}
