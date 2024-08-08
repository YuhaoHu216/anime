package hu.news.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Anime {
    private Integer id;
    private String name;
    private String date;
    private String bangumiScore;
    private String episode;
    private String condition;
}
