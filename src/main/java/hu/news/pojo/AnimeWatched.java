package hu.news.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnimeWatched {
    private String watchTime;
    private String name;
    private String broadcastTime;
    private String bangumiScore;
    private String episode;
    private String year;
}
