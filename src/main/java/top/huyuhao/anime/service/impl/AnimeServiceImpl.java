package top.huyuhao.anime.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import top.huyuhao.anime.context.UserContext;
import top.huyuhao.anime.mapper.AnimeMapper;
import top.huyuhao.anime.mapper.TagMapper;
import top.huyuhao.anime.pojo.Anime;
import top.huyuhao.anime.pojo.PageBean;
import top.huyuhao.anime.pojo.Result;
import top.huyuhao.anime.pojo.Tag;
import top.huyuhao.anime.pojo.dto.BangumiInfo;
import top.huyuhao.anime.service.AnimeService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AnimeServiceImpl implements AnimeService {

    @Autowired
    private AnimeMapper animeMapper;

    @Autowired
    private TagMapper tagMapper;

    // Bangumi 代理配置（翻墙访问）
    @Value("${bangumi.proxy.host:}")
    private String proxyHost;

    @Value("${bangumi.proxy.port:0}")
    private int proxyPort;

    private RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern BANGUMI_URL_PATTERN =
            Pattern.compile("(?:bangumi\\.tv|bgm\\.tv)/subject/(\\d+)");

    @PostConstruct
    public void initRestTemplate() {
        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            java.net.Proxy proxy = new java.net.Proxy(
                    java.net.Proxy.Type.HTTP,
                    new java.net.InetSocketAddress(proxyHost, proxyPort));
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setProxy(proxy);
            this.restTemplate = new RestTemplate(factory);
        } else {
            this.restTemplate = new RestTemplate();
        }
    }

    @Override
    public PageBean<Anime> search(Integer page, Integer pageSize, String name, Integer tagId) {
        PageHelper.startPage(page, pageSize);
        List<Anime> animeList = animeMapper.search(name, tagId);
        for (Anime anime : animeList) {
            anime.setTags(tagMapper.findByAnimeId(anime.getId()));
        }
        Page<Anime> p = (Page<Anime>) animeList;
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    public Result findById(Integer id) {
        Anime anime = animeMapper.findById(id);
        if (anime == null) {
            throw new RuntimeException("动漫不存在");
        }
        anime.setTags(tagMapper.findByAnimeId(id));
        return Result.success(anime);
    }

    @Override
    @Transactional
    public Integer prepareAnime() {
        Anime anime = new Anime();
        animeMapper.prepareInsert(anime);
        return anime.getId();
    }

    @Override
    @Transactional
    public Result addAnime(Anime anime, List<Integer> tagIds) {
        anime.setReviewStatus("approved");
        anime.setSubmittedBy(UserContext.getUserId());
        animeMapper.update(anime);
        if (tagIds != null) {
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success();
    }

    @Override
    @Transactional
    public Result updateAnime(Anime anime, List<Integer> tagIds) {
        animeMapper.update(anime);
        if (tagIds != null) {
            tagMapper.unlinkAllTags(anime.getId());
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success("更新成功");
    }

    @Override
    @Transactional
    public Result deleteAnime(Integer id) {
        tagMapper.unlinkAllTags(id);
        animeMapper.delete(id);
        return Result.success("删除成功");
    }

    @Override
    @Transactional
    public Result submitAnime(Anime anime, List<Integer> tagIds, Integer userId) {
        anime.setReviewStatus("pending");
        anime.setSubmittedBy(userId);
        animeMapper.update(anime);
        if (tagIds != null) {
            for (Integer tagId : tagIds) {
                tagMapper.linkTag(anime.getId(), tagId);
            }
        }
        return Result.success("提交成功，等待管理员审核");
    }

    @Override
    public PageBean<Anime> getMySubmissions(Integer page, Integer pageSize, String reviewStatus, Integer userId) {
        PageHelper.startPage(page, pageSize);
        List<Anime> list = animeMapper.searchBySubmitter(userId, reviewStatus);
        Page<Anime> p = (Page<Anime>) list;
        return new PageBean<>(p.getTotal(), p.getResult());
    }

    @Override
    public BangumiInfo parseBangumiInfo(String bangumiUrl) {
        Matcher matcher = BANGUMI_URL_PATTERN.matcher(bangumiUrl);
        if (!matcher.find()) {
            throw new RuntimeException("无法识别的 Bangumi 地址");
        }
        String subjectId = matcher.group(1);

        String apiUrl = "https://api.bgm.tv/v0/subjects/" + subjectId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "AnimeApp/1.0");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
        } catch (Exception e) {
            throw new RuntimeException("无法连接 Bangumi API，请稍后重试");
        }

        try {
            JsonNode root = objectMapper.readTree(response.getBody());
            BangumiInfo.BangumiInfoBuilder b = BangumiInfo.builder();

            if (root.has("name_cn") && !root.get("name_cn").isNull())
                b.nameCn(root.get("name_cn").asText());
            if (root.has("name") && !root.get("name").isNull())
                b.nameJp(root.get("name").asText());
            if (root.has("summary") && !root.get("summary").isNull())
                b.description(root.get("summary").asText().replaceAll("<[^>]*>", "").trim());
            if (root.has("date") && !root.get("date").isNull())
                b.broadcastTime(root.get("date").asText());
            if (root.has("eps") && !root.get("eps").isNull())
                b.episode(root.get("eps").asInt());
            if (root.has("rating") && root.get("rating").has("score")
                    && !root.get("rating").get("score").isNull()) {
                b.bangumiScore(String.format("%.1f", root.get("rating").get("score").asDouble()));
            }
            b.bangumiUrl("https://bangumi.tv/subject/" + subjectId);

            if (root.has("images")) {
                JsonNode imgs = root.get("images");
                if (imgs.has("large") && !imgs.get("large").isNull())
                    b.coverUrl(imgs.get("large").asText());
                else if (imgs.has("common") && !imgs.get("common").isNull())
                    b.coverUrl(imgs.get("common").asText());
            }

            if (root.has("infobox") && root.get("infobox").isArray()) {
                for (JsonNode item : root.get("infobox")) {
                    String key = item.has("key") ? item.get("key").asText() : "";
                    if ("官方网站".equals(key) || "官网".equals(key)) {
                        JsonNode value = item.get("value");
                        if (value != null && value.isTextual()) {
                            b.officialWebsite(value.asText());
                        }
                        break;
                    }
                }
            }

            return b.build();
        } catch (Exception e) {
            throw new RuntimeException("解析 Bangumi 数据失败: " + e.getMessage());
        }
    }

    @Override
    public Resource proxyImage(String imageUrl) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "AnimeApp/1.0");
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<byte[]> resp = restTemplate.exchange(
                    imageUrl, HttpMethod.GET, entity, byte[].class);
            if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null)
                throw new RuntimeException("下载图片失败");
            return new ByteArrayResource(resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("图片下载失败: " + e.getMessage());
        }
    }
}
