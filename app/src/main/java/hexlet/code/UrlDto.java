package hexlet.code;

import hexlet.code.model.UrlCheck;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Setter
@Getter
public final class UrlDto {
    private long id;
    private String name;
    private Instant createdAt;
    private List<UrlCheck> urlCheckList;

    public Instant getLastCheckDate() {
        if (!urlCheckList.isEmpty()) {
            return urlCheckList.get(urlCheckList.size() - 1).getCreatedAt();
        }
        return null;
    }
    public Integer getLastCheckStatus() {
        if (!urlCheckList.isEmpty()) {
            return urlCheckList.get(urlCheckList.size() - 1).getStatusCode();
        }
        return null;
    }
}
