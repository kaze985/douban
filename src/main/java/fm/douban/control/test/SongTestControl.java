package fm.douban.control.test;

import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/test/song")
public class SongTestControl {
    @Autowired
    private SongService songService;

    @GetMapping(path = "/add")
    public Song testAdd(){
        Song song = new Song();
        song.setId("0");
        song.setName("李鹏鹏牛逼");
        Song addSong = songService.add(song);
        return addSong;
    }

    @GetMapping(path = "/list")
    public Page<Song> testList(){
        SongQueryParam songParam = new SongQueryParam();
        songParam.setPageNum(1);
        songParam.setPageSize(1);
        Page<Song> songs = songService.list(songParam);
        return songs;
    }

    @GetMapping(path = "/get")
    public Song testGet(){
        Song song = songService.get("0");
        return song;
    }

    @GetMapping(path = "/modify")
    public boolean testModify(){
        Song song = new Song();
        song.setId("0");
        song.setName("李鹏鹏真他妈牛逼");
        boolean modify = songService.modify(song);
        return modify;
    }

    @GetMapping(path = "/del")
    public boolean testDelete(){
        boolean delete = songService.delete("0");
        return delete;
    }

}
