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

    public UrlCheck(int newStatusCode, String newTitle, String newH1, String newDescription, Url newUrl) {
        this.statusCode = newStatusCode;
        this.title = newTitle;
        this.h1 = newH1;
        this.description = newDescription;
        this.url = newUrl;
    }
}
