package fm.douban.service;

import fm.douban.model.User;
import fm.douban.param.UserQueryParam;
import org.springframework.data.domain.Page;

public interface UserService {
    User add(User user);
    User get(String id);
    Page<User> list(UserQueryParam param);
    boolean modify(User user);
    boolean delete(String id);
    User getUserByLoginName(String loginName);
}
