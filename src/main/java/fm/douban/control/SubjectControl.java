package fm.douban.control;

import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.service.impl.UserServiceImpl;
import fm.douban.util.SubjectUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
public class SubjectControl {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private SingerService singerService;
    @Autowired
    private SongService songService;

    @GetMapping(path = "/artist")
    public String mhzDetail(Model model, @RequestParam String subjectId){
        Subject subject = subjectService.get(subjectId);
        model.addAttribute("subject",subject);

        List<String> songIds = subject.getSongIds();
        List<Song> songs=new ArrayList<>();
        if(!CollectionUtils.isEmpty(songIds)){
            for(String songId:songIds){
                songs.add(songService.get(songId));
            }
        }
        model.addAttribute("songs",songs);

        String master = subject.getMaster();
        //Singer singer1=null;
        List<Singer> all = singerService.getAll();
        List<String> simSingerIds=new ArrayList<>();
        for(Singer singer:all){
            String singerName = singer.getName();
            if(master!=null&&singerName!=null&& StringUtils.equals(master,singerName+" 系")){
               simSingerIds=singer.getSimilarSingerIds();
               //singer1=singer;
            }
        }
        List<Singer> simSingers=new ArrayList<>();
        for(String singerId:simSingerIds){
            simSingers.add(singerService.get(singerId));
        }
        model.addAttribute("simSingers",simSingers);
        //model.addAttribute("singer",singer1);
        return "mhzdetail";
    }

    @GetMapping(path = "/collection")
    public String collection(Model model){
        List<Subject> collections = subjectService.getSubjects(SubjectUtil.TYPE_COLLECTION);
        model.addAttribute("collections",collections);
        return "collection";
    }

    @GetMapping(path = "/collectiondetail")
    public String collectionDetail(Model model,@RequestParam String subjectId){
        Subject subject = subjectService.get(subjectId);
        if(subject==null){
            return null;
        }
        model.addAttribute("subject",subject);

        Singer singerParam = new Singer();
        singerParam.setName(subject.getMaster());
        List<Singer> singers = singerService.getSingers(singerParam);
        if(!singers.isEmpty()){
            model.addAttribute("singer",singers.get(0));
        }

        List<Song> songs=new ArrayList<>();
        List<String> songIds = subject.getSongIds();
        if(CollectionUtils.isEmpty(songIds)){
            return null;
        }
        for (String songId : songIds) {
            if(songService.get(songId)!=null) {
                songs.add(songService.get(songId));
            }
        }
        model.addAttribute("songs",songs);

        Subject subject1 = new Subject();
        subject1.setMaster(subject.getMaster());
        subject1.setSubjectType(SubjectUtil.TYPE_COLLECTION);
        List<Subject> subjects = subjectService.getSubjects(subject1);
        model.addAttribute("otherSubjects",subjects);

        return "collectiondetail";
    }

}
