package fm.douban.control;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class SingerControl {
    @Autowired
    private SingerService singerService;

    @GetMapping(path = "/user-guide")
    public String myMhz(Model model){
        List<Singer> randomSingers = randomSingers();
        model.addAttribute("singers",randomSingers);
        return "userguide";
    }

    @GetMapping(path = "/singer/random")
    @ResponseBody
    public List<Singer> randomSingers() {
        List<Singer> all = singerService.getAll();
        List<Singer> tenSingers=new ArrayList<>();
        for(int i=0;i<10;i++) {
            int index=(int)(Math.random()*(all.size()+1));
            if(index<=all.size()){
                tenSingers.add(all.get(index));
            }
        }
        return tenSingers;
    }
}
