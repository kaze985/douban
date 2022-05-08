package fm.douban.control;

import fm.douban.model.*;
import fm.douban.param.SongQueryParam;
import fm.douban.service.FavoriteService;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.FavoriteUtil;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class MainControl {
    @Autowired
    private SongService songService;
    @Autowired
    private SingerService singerService;
    @Autowired
    private SubjectService subjectService;
    @Autowired
    private FavoriteService favoriteService;
    //跳转首页
    @RequestMapping(path = "/")
    public void defaultView(HttpServletResponse response) {
        String url="/index";
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //首页
    @GetMapping(path = "/index")
    public String index(Model model){
        SongQueryParam queryParam = new SongQueryParam();
        Random random = new Random();
        queryParam.setPageSize(1);
        queryParam.setPageNum(random.nextInt(100)+1);
        Page<Song> songs = songService.list(queryParam);
        List<Song> content = songs.getContent();
        if (content == null) {
            return "index";
        }
        Song song = content.get(0);

        model.addAttribute("song",song);

        List<String> singerIds = song.getSingerIds();
        List<Singer> singers=new ArrayList<>();
        for(String singerId:singerIds){
            singers.add(singerService.get(singerId));
        }
        model.addAttribute("singers",singers);

        List<Subject> ageList =new ArrayList<>();
        List<Subject> styleList=new ArrayList<>();
        List<Subject> artistList=new ArrayList<>();
        List<Subject> moodList=new ArrayList<>();
        List<Subject> mhzSubjects = subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
        for (Subject subject:mhzSubjects){
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_AGE)){
                ageList.add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_STYLE)){
                styleList.add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_MOOD)){
                moodList.add(subject);
            }
            if(subject.getSubjectSubType().equals(SubjectUtil.TYPE_SUB_ARTIST)){
                artistList.add(subject);
            }
        }
        MhzViewModel ageViewModel = new MhzViewModel();
        MhzViewModel moodViewModel = new MhzViewModel();
        MhzViewModel styleViewModel = new MhzViewModel();
        ageViewModel.setTitle("语言 / 年代");
        ageViewModel.setSubjects(ageList);
        moodViewModel.setTitle("心情 / 场景");
        moodViewModel.setSubjects(moodList);
        styleViewModel.setTitle("风格 / 流派");
        styleViewModel.setSubjects(styleList);
        List<MhzViewModel> mhzViewModels=new ArrayList<>();
        mhzViewModels.add(moodViewModel);
        mhzViewModels.add(ageViewModel);
        mhzViewModels.add(styleViewModel);
        model.addAttribute("mhzViewModels",mhzViewModels);

        Collections.shuffle(artistList);
        model.addAttribute("artistDatas",artistList);
        return "index";
    }

    //搜索页(只能准确搜索）
    @GetMapping(path = "/search")
    public String search(Model model){
        return "search";
    }

    //搜索结果
    @GetMapping(path = "/searchContent")
    @ResponseBody
    public Map searchContent(@RequestParam String keyword){
        Map result=new HashMap();
        SongQueryParam queryParam = new SongQueryParam();
        queryParam.setName(keyword);
        queryParam.setPageSize(5);
        Page<Song> songPage = songService.list(queryParam);
        List<Song> songs = songPage.getContent();
        result.put("songs",songs);
        return result;
    }

    //我的页面(暂时只显示红心歌曲）
    @GetMapping(path = "/my")
    public String myPage(Model model, HttpServletRequest request, HttpServletResponse response){
        UserLoginInfo userLoginInfo =(UserLoginInfo) request.getSession().getAttribute("userLoginInfo");
        String userId = userLoginInfo.getUserId();
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setType(FavoriteUtil.TYPE_RED_HEART);
        List<Favorite> favoriteList = favoriteService.list(favorite);
        model.addAttribute("favorite",favoriteList);

        List<String> songIds=new ArrayList<>();
        List<Song> songs=new ArrayList<>();
        favorite.setItemType(FavoriteUtil.ITEM_TYPE_SONG);
        List<Favorite> favoriteList1 = favoriteService.list(favorite);
        for(Favorite favorite1:favoriteList1){
            songIds.add(favorite1.getItemId());
        }
        for(String songId:songIds){
            songs.add(songService.get(songId));
        }
        model.addAttribute("songs",songs);
        return "my";
    }

    //添加红心
    @GetMapping(path = "/fav")
    @ResponseBody
    public Map doFav(@RequestParam String itemType,@RequestParam String itemId,HttpServletRequest request, HttpServletResponse response){
        Map returnData=new HashMap();
        UserLoginInfo userLoginInfo =(UserLoginInfo) request.getSession().getAttribute("userLoginInfo");
        String userId = userLoginInfo.getUserId();
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setType(FavoriteUtil.TYPE_RED_HEART);
        favorite.setItemType(itemType);
        favorite.setItemId(itemId);
        List<Favorite> list = favoriteService.list(favorite);
        if(list.isEmpty()){
            favorite.setGmtCreated(LocalDateTime.now());
            favorite.setGmtCreated(LocalDateTime.now());
            favoriteService.add(favorite);
        }else{
            for(Favorite favorite1:list){
                favoriteService.delete(favorite1);
            }
        }
        returnData.put("message","successful");
        
        return returnData;
    }

}
