package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.User;
import fm.douban.param.UserQueryParam;
import fm.douban.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

@Service
public class UserServiceImpl implements UserService {
  @Autowired
  private MongoTemplate mongoTemplate;


  private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

  @Override
  public User add(User user) {
    return mongoTemplate.insert(user);
  }

  @Override
  public User get(String id) {
    return mongoTemplate.findById(id, User.class);
  }

  @Override
  public Page<User> list(UserQueryParam param) {
    if (param == null) {
      LOG.error("input user data is not correct.");
      return null;
    }

    // 总条件
    Criteria criteria = new Criteria();
    // 可能有多个子条件
    List<Criteria> subCris = new ArrayList();
    if (StringUtils.hasText(param.getLoginName())) {
      subCris.add(Criteria.where("loginName").is(param.getLoginName()));
    }

    if (StringUtils.hasText(param.getMobile())) {
      subCris.add(Criteria.where("mobile").is(param.getMobile()));
    }

    if (StringUtils.hasText(param.getId())) {
      subCris.add(Criteria.where("id").is(param.getId()));
    }

    // 必须至少有一个查询条件
    if (subCris.isEmpty()) {
      LOG.error("input user query param is not correct.");
      return null;
    }

    // 三个子条件以 and 关键词连接成总条件对象，相当于 name='' and lyrics='' and subjectId=''

      criteria.andOperator(subCris.toArray(new Criteria[]{}));



    // 条件对象构建查询对象
    Query query = new Query(criteria);
    List<User> users = mongoTemplate.find(query, User.class);
    Pageable pageable=PageRequest.of(param.getPageNum()-1,param.getPageSize());
    query.with(pageable);
    long count=mongoTemplate.count(query,User.class);
    Page<User> pageResult = PageableExecutionUtils.getPage(users, pageable, new LongSupplier() {
      @Override
      public long getAsLong() {
        return count;
      }
    });
    return pageResult;
  }



  @Override
  public boolean modify(User user) {
    if(user==null){
      return false;
    }
    Query query = new Query(Criteria.where("id").is(user.getId()));
    Update update = new Update();
    if (StringUtils.hasText(user.getPassword())){
      update.set("password",user.getPassword());
    }
    if (StringUtils.hasText(user.getMobile())){
      update.set("mobile",user.getMobile());
    }

    UpdateResult result = mongoTemplate.updateFirst(query, update, User.class);

    System.out.println("修改的数据记录数量：" + result.getModifiedCount());
    return result!=null&&result.getModifiedCount()>0;
  }

  @Override
  public boolean delete(String id) {
    User user = new User();
    user.setId(id);

// 执行删除
    DeleteResult result = mongoTemplate.remove(user);
// 删除的记录数大于 0 ，表示删除成功
    System.out.println("删除的数据记录数量：" + result.getDeletedCount());
    return result!=null&&result.getDeletedCount()>0;
  }

  @Override
  public User getUserByLoginName(String loginName) {
    UserQueryParam param = new UserQueryParam();
    param.setLoginName(loginName);
    Page<User> list =list(param);
    List<User> content = list.getContent();
    if(content.isEmpty()){
      return null;
    }
      return content.get(0);
  }
}
