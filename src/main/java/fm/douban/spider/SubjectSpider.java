package fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.HttpUtil;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class SubjectSpider {
    @Autowired
    private SubjectService subjectService;
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
        getSubjectData();
        getCollectionsData();
        return true;
    }

    public void getSubjectData() {
        Map<String, String> headerData = httpUtil.buildHeaderData("", "fm.douban.com", "bid=KqLVwUyVCfY; __utma=30149280.818165923.1637991623.1637991623.1637991623.1; __utmz=30149280.1637991623.1.1.utmcsr=accounts.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=30149280.25077; _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1643172826%2C%22https%3A%2F%2Flearn.youkeda.com%2F%22%5D; _ga=GA1.2.818165923.1637991623; _pk_id.100001.f71f=afac0759a49c7e52.1643172826.1.1643173366.1643172826.");
        String content = httpUtil.getContent("https://fm.douban.com/j/v2/rec_channels?specific=all", headerData);
        Map map_1 = JSON.parseObject(content, Map.class);
        Map data = (Map) map_1.get("data");
        Map channels = (Map) data.get("channels");
        //爬取主题
        setSubject(channels, SubjectUtil.TYPE_MHZ, SubjectUtil.TYPE_SUB_MOOD, "scenario");
        setSubject(channels, SubjectUtil.TYPE_MHZ, SubjectUtil.TYPE_SUB_AGE, "language");
        setSubject(channels, SubjectUtil.TYPE_MHZ, SubjectUtil.TYPE_SUB_ARTIST, "artist");
        setSubject(channels, SubjectUtil.TYPE_MHZ, SubjectUtil.TYPE_SUB_STYLE, "genre");
        //爬取主题相关歌手
        List<Map> channel = (List) channels.get("artist");
        channel.forEach((Map map_2) -> {
            List<Map> singers = (List) map_2.get("related_artists");
            singers.forEach((Map map_3) -> {
                Singer singer = new Singer();
                singer.setName((String) map_3.get("name"));
                singer.setId((String) map_3.get("id"));
                singer.setAvatar((String) map_3.get("cover"));
                singer.setGmtCreated(LocalDateTime.now());
                singer.setGmtModified(LocalDateTime.now());
                if (singerService.get(singer.getId()) == null) {
                    singerService.addSinger(singer);
                }
            });
        });
    }

    public void setSubject(Map channels, String type, String typeSub, String channelName) {
        List<Map> channel = (List) channels.get(channelName);

        channel.forEach((Map map) -> {
            Map creator = (Map) map.get("creator");
            Subject subject = new Subject();

            subject.setSubjectType(type);
            subject.setSubjectSubType(typeSub);
            subject.setMaster((String) creator.get("name"));
            subject.setCover((String) map.get("cover"));
            subject.setName((String) map.get("name"));
            subject.setDescription((String) map.get("intro"));
            subject.setId(String.valueOf(map.get("id")));
            subject.setPublishedDate(LocalDate.now());
            subject.setGmtCreated(LocalDateTime.now());
            subject.setGmtModified(LocalDateTime.now());

            if (subjectService.get(subject.getId()) == null) {
                subjectService.addSubject(subject);
                getSubjectSongData(subject);
            }
        });


    }

    public void getSubjectSongData(Subject subject) {
        String url = "https://fm.douban.com/j/v2/playlist?channel=" + subject.getId() + "&kbps=128&client=s%3Amainsite%7Cy%3A3.0&app_name=radio_website&version=100&type=n";
        Map<String, String> headerData = httpUtil.buildHeaderData("", "fm.douban.com", "bid=KqLVwUyVCfY; __utma=30149280.818165923.1637991623.1637991623.1637991623.1; __utmz=30149280.1637991623.1.1.utmcsr=accounts.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=30149280.25077; _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1643172826%2C%22https%3A%2F%2Flearn.youkeda.com%2F%22%5D; _ga=GA1.2.818165923.1637991623; _pk_id.100001.f71f=afac0759a49c7e52.1643172826.1.1643173366.1643172826.");
        String content = httpUtil.getContent(url, headerData);
        Map map_1 = JSON.parseObject(content, Map.class);
        //爬取歌曲
        if (map_1 == null) {
            return;
        }
        List<Map> songs = (List) map_1.get("song");

        List<String> songIds = new ArrayList<>();

        songs.forEach((Map song) -> {
            Song song1 = new Song();
            List<String> singerIds = new ArrayList<>();
            List<Map> singers = (List) song.get("singers");
            singers.forEach((Map singer) -> {
                singerIds.add((String) singer.get("id"));
                //爬取歌曲所属歌手
                Singer singer1 = new Singer();
                singer1.setId((String) singer.get("id"));
                singer1.setAvatar((String) singer.get("avatar"));
                singer1.setName((String) singer.get("name"));
                singer1.setGmtCreated(LocalDateTime.now());
                singer1.setGmtModified(LocalDateTime.now());
                if (singerService.get(singer1.getId()) == null) {
                    singerService.addSinger(singer1);
                }
            });

            song1.setSingerIds(singerIds);
            song1.setUrl((String) song.get("url"));
            song1.setName((String) song.get("title"));
            song1.setId((String) song.get("sid"));
            song1.setCover((String) song.get("picture"));
            song1.setGmtCreated(LocalDateTime.now());
            song1.setGmtModified(LocalDateTime.now());

            songIds.add((String) song.get("sid"));
            if (songService.get(song1.getId()) == null) {
                songService.add(song1);
            }
        });

        subject.setSongIds(songIds);
        subjectService.modify(subject);


    }

    public void getCollectionsData() {
        String url = "https://fm.douban.com/j/v2/songlist/explore?type=hot&genre=0&limit=20&sample_cnt=5";
        Map<String, String> headerData = httpUtil.buildHeaderData("", "fm.douban.com", "bid=KqLVwUyVCfY; __utma=30149280.818165923.1637991623.1637991623.1637991623.1; __utmz=30149280.1637991623.1.1.utmcsr=accounts.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmv=30149280.25077; _pk_ref.100001.f71f=%5B%22%22%2C%22%22%2C1643172826%2C%22https%3A%2F%2Flearn.youkeda.com%2F%22%5D; _ga=GA1.2.818165923.1637991623; _pk_id.100001.f71f=afac0759a49c7e52.1643172826.1.1643173366.1643172826.");
        String content = httpUtil.getContent(url, headerData);
        List<Map> list = JSON.parseObject(content, List.class);

        //爬取歌单信息
        list.forEach((Map map_1) -> {
            Map map_2 = (Map) map_1.get("creator");
            Subject subject = new Subject();
            subject.setSubjectType(SubjectUtil.TYPE_COLLECTION);
            subject.setId(String.valueOf(map_1.get("id")));
            subject.setCover((String) map_1.get("cover"));
            subject.setDescription((String) map_1.get("intro"));
            subject.setName((String) map_1.get("title"));
            subject.setMaster((String) map_2.get("name"));


            subject.setPublishedDate(LocalDate.now());

            subject.setGmtModified(LocalDateTime.now());
            subject.setGmtCreated(LocalDateTime.now());
            if (subjectService.get(subject.getId()) == null) {
                subjectService.addSubject(subject);
            }
            //爬取歌单创建者
            Singer singer = new Singer();
            singer.setId((String) map_2.get("id"));
            singer.setName((String) map_2.get("name"));
            singer.setAvatar((String) map_2.get("picture"));
            singer.setHomepage((String) map_2.get("url"));
            if (singerService.get(singer.getId()) == null) {
                singerService.addSinger(singer);
            }
        });

    }

}


