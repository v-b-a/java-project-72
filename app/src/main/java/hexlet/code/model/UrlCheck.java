package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.time.Instant;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public final class UrlCheck extends Model {
    @Id
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
