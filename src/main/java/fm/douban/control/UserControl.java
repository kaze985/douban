package fm.douban.control;

import fm.douban.model.User;
import fm.douban.model.UserLoginInfo;
import fm.douban.param.UserQueryParam;
import fm.douban.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class UserControl {

  private static final Logger LOG = LoggerFactory.getLogger(UserControl.class);

  @Autowired
  private UserService userService;

  @PostConstruct
  public void init() {
    LOG.info("UserControl 启动啦");
    LOG.info("userService 注入啦");
  }

  @GetMapping(path = "/login")
  public String loginPage(Model model) {
    return "login";
  }

  @PostMapping(path = "/authenticate")
  @ResponseBody
  public Map login(@RequestParam String name, @RequestParam String password, HttpServletRequest request,
                   HttpServletResponse response) {
    Map returnData = new HashMap();
    // 根据登录名查询用户
    User regedUser = userService.getUserByLoginName(name);

    // 找不到此登录用户
    if (regedUser == null) {
      returnData.put("result", false);
      returnData.put("message", "userName not correct");
      return returnData;
    }

    if (regedUser.getPassword().equals(password)) {
      UserLoginInfo userLoginInfo = new UserLoginInfo();
      userLoginInfo.setUserId(regedUser.getId());
      userLoginInfo.setUserName(name);
      // 取得 HttpSession 对象
      HttpSession session = request.getSession();
      // 写入登录信息
      session.setAttribute("userLoginInfo", userLoginInfo);
      returnData.put("result", true);
      returnData.put("message", "login successfule");
    } else {
      returnData.put("result", false);
      returnData.put("message", "userName or password not correct");
    }

    return returnData;
  }

  @GetMapping(path = "/sign")
  public String signPage(Model model) {
    return "sign";
  }

  @PostMapping(path = "/register")
  @ResponseBody
  public Map registerAction(@RequestParam String name, @RequestParam String password, @RequestParam String mobile,
                            HttpServletRequest request, HttpServletResponse response) {
    Map returnData = new HashMap<>();
    User user1=new User();
    UserQueryParam queryParam = new UserQueryParam();
    queryParam.setLoginName(name);
    Page<User> userPage = userService.list(queryParam);
    List<User> content = userPage.getContent();
    if(!content.isEmpty()){
      returnData.put("message","login name already exist");
    }else{
      User user = new User();
      user.setLoginName(name);
      user.setPassword(password);
      user.setMobile(mobile);
      user1 = userService.add(user);
    }
    if(user1.getId().isEmpty()){
      returnData.put("message","register failed");
    }else{
      returnData.put("message","register success");
    }
    return returnData;
  }


}
