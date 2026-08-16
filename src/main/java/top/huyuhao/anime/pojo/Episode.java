package top.huyuhao.anime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Episode {
    private Integer id;
    private Integer animeId;
    private String epNo;
    private String name;
    private String airDate;
    private Integer duration;
    private Double sort;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
