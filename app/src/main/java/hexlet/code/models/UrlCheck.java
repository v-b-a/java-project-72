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
    private int statusCode;
    private String title;
    private String h1;
    @Lob
    private String description;
    @ManyToOne(cascade = CascadeType.ALL)
    private Url url;
    @WhenCreated
    private Instant createdAt;

    public UrlCheck(int status, String titleT, String h1N, String descriptionN, Url urlN) {
        this.statusCode = status;
        this.title = titleT;
        this.h1 = h1N;
        this.description = descriptionN;
        this.url = urlN;
    }
}
