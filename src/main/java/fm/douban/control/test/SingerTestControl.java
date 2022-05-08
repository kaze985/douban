package fm.douban.control.test;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/test/singer")
public class SingerTestControl {
    @Autowired
    private SingerService singerService;

    @GetMapping(path = "/add")
    public Singer testAddSinger(){
        Singer singer = new Singer();
        singer.setId("0");
        singer.setName("李鹏鹏牛逼");
        Singer addSinger = singerService.addSinger(singer);
        return addSinger;
    }

    @GetMapping(path = "/getAll")
    public List<Singer> testGetAll(){
        List<Singer> all = singerService.getAll();
        return all;
    }

    @GetMapping(path = "/getOne")
    public Singer testGetSinger(){
        Singer singer = singerService.get("0");
        return singer;
    }

    @GetMapping(path = "/modify")
    public boolean testModifySinger(){
        Singer singer = new Singer();
        singer.setId("0");
        singer.setName("李鹏鹏真他妈牛逼");
        boolean modify = singerService.modify(singer);
        return modify;
    }

    @GetMapping(path = "/del")
    public boolean testDelSinger(){
        boolean delete = singerService.delete("0");
        return delete;
    }

}
