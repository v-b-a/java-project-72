package hexlet.code.models;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public final class Url extends Model {
    @Id
    private long id;

    private String name;

    @WhenCreated
    private Instant createAt;

    @OneToMany(cascade = CascadeType.ALL)
    List<UrlCheck> urlCheckList;

    public Url(String url) {
        this.name = url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Url url = (Url) o;
        return id == url.id && Objects.equals(name, url.name) && Objects.equals(createAt, url.createAt) && Objects.equals(urlCheckList, url.urlCheckList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, createAt, urlCheckList);
    }
}
