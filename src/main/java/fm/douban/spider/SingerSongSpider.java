package fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class SingerSongSpider {

    @Autowired
    private SingerService singerService;
    @Autowired
    private SongService songService;
    @Autowired
    private HttpUtil httpUtil;

    //@PostConstruct
    public void init() {
        CompletableFuture.supplyAsync(() -> doExcute())
                .thenAccept(result -> {
                    if (result) {
                        System.out.println("spider end ...");
                    }
                });
    }

    public boolean doExcute() {
        getSongDataBySingers();
        return true;
    }

    public void getSongDataBySingers() {
        List<Singer> all = singerService.getAll();
        Map<String, String> headerData = httpUtil.buildHeaderData("", "fm.douban.com", "bid=KqLVwUyVCfY; __utma=30149280.818165923.1637991623.1637991623.1637991623.1; __utmz=30149280.1637991623.1.1.utmcsr=accounts.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=30149280.25077; _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1643172826%2C%22https%3A%2F%2Flearn.youkeda.com%2F%22%5D; _ga=GA1.2.818165923.1637991623; _pk_id.100001.f71f=afac0759a49c7e52.1643172826.1.1643173366.1643172826.");
        all.forEach((Singer singer) -> {
            String url = "https://fm.douban.com/j/v2/artist/" + singer.getId();
            String content = httpUtil.getContent(url, headerData);
            Map map_1 = JSON.parseObject(content, Map.class);
            //爬取相似的歌手
            List<String> similarSingerIds = getSimilarSingers(map_1);
            Singer singer1 = new Singer();
            singer1.setSimilarSingerIds(similarSingerIds);
            singer1.setId(singer.getId());
            singerService.modify(singer1);
            //爬取歌手歌曲
            if (map_1 == null) {
                return;
            }
            Map map_2 = (Map) map_1.get("songlist");
            List<Map> map_3 = (List) map_2.get("songs");
            map_3.forEach((Map map_4) -> {
                Song song = new Song();
                List<String> singerIds = new ArrayList<>();
                List<Map> map_5 = (List) map_4.get("singers");
                map_5.forEach((Map map_6) -> {
                    singerIds.add((String) map_6.get("id"));
                });
                song.setSingerIds(singerIds);
                song.setCover((String) map_4.get("picture"));
                song.setId((String) map_4.get("sid"));
                song.setUrl((String) map_4.get("url"));
                song.setName((String) map_4.get("title"));
                song.setGmtCreated(LocalDateTime.now());
                song.setGmtModified(LocalDateTime.now());

                if (songService.get(song.getId()) == null) {
                    songService.add(song);
                }
            });
        });

    }

    public List<String> getSimilarSingers(Map map_1) {
        if (map_1 == null) {
            return new ArrayList<>();
        }
        Map map_2 = (Map) map_1.get("related_channel");
        if (map_2 == null) {
            return new ArrayList<>();
        }
        List<Map> map_3 = (List) map_2.get("similar_artists");
        List<String> similarSingerIds = new ArrayList<>();

        map_3.forEach((Map map_4) -> {
            Singer singer = new Singer();
            singer.setName((String) map_4.get("name"));
            singer.setAvatar((String) map_4.get("avatar"));
            singer.setId((String) map_4.get("id"));
            singer.setGmtCreated(LocalDateTime.now());
            singer.setGmtModified(LocalDateTime.now());

            similarSingerIds.add(singer.getId());

            if (singerService.get(singer.getId()) == null) {
                singerService.addSinger(singer);
            }
        });
        return similarSingerIds;

    }
}
