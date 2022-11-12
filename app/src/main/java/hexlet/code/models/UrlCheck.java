package hexlet.code.models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import java.time.Instant;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Setter
@Getter
public class UrlCheck extends Model {
    @Id
    private long id;
    private int statusCode;
    private String title;
    private String h1;
    @Lob
    private String description;
    @ManyToOne
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UrlCheck urlCheck = (UrlCheck) o;
        return id == urlCheck.id && statusCode == urlCheck.statusCode && Objects.equals(title, urlCheck.title) && Objects.equals(h1, urlCheck.h1) && Objects.equals(description, urlCheck.description) && Objects.equals(url, urlCheck.url) && Objects.equals(createdAt, urlCheck.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, statusCode, title, h1, description, url, createdAt);
    }
}
