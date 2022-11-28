package hexlet.code.model;

import io.ebean.Model;
import io.ebean.annotation.WhenCreated;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;

@Entity
@Setter
@Getter
public final class Url extends Model {
    @Id
    private long id;

    private String name;

    @WhenCreated
    private Instant createAt;

    @OneToMany(cascade = CascadeType.ALL)
    private List<UrlCheck> urlCheckList;

    public Url(String url) {
        this.name = url;
    }
}
