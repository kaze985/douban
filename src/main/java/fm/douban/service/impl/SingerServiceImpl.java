package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
@Service
public class SingerServiceImpl implements SingerService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Singer addSinger(Singer singer) {
        return mongoTemplate.insert(singer);
    }

    @Override
    public Singer get(String singerId) {
        return mongoTemplate.findById(singerId,Singer.class);
    }

    @Override
    public List<Singer> getAll() {
        return mongoTemplate.findAll(Singer.class);
    }

    @Override
    public boolean modify(Singer singer) {
        if(singer==null){
            return false;
        }
        Query query = new Query(Criteria.where("id").is(singer.getId()));
        Update update = new Update();
        if (StringUtils.hasText(singer.getAvatar())){
            update.set("avatar",singer.getAvatar());
        }
        if (StringUtils.hasText(singer.getHomepage())){
            update.set("homepage",singer.getHomepage());
        }
        if (StringUtils.hasText(singer.getName())){
            update.set("name",singer.getName());
        }
        if (!singer.getSimilarSingerIds().isEmpty()){
            update.set("similarSingerIds",singer.getSimilarSingerIds());
        }
        UpdateResult result = mongoTemplate.updateFirst(query, update, Singer.class);
        return result!=null&&result.getModifiedCount()>0;
    }

    @Override
    public boolean delete(String singerId) {
        Singer singer = new Singer();
        singer.setId(singerId);
        DeleteResult result = mongoTemplate.remove(singer);
        return result!=null&&result.getDeletedCount()>0;
    }

    @Override
    public List<Singer> getSingers(Singer singerParam) {
        if (singerParam == null) {
            LOG.error("input singerParam is not correct.");
            return null;
        }

        Criteria criteria = new Criteria();

        List<Criteria> subCris=new ArrayList<>();

        if(StringUtils.hasText(singerParam.getName())){
            subCris.add(Criteria.where("name").is(singerParam.getName()));
        }
        if(StringUtils.hasText(singerParam.getId())){
            subCris.add(Criteria.where("id").is(singerParam.getId()));
        }

        if(!subCris.isEmpty()){
            criteria.andOperator(subCris.toArray(new Criteria[]{}));
        }
        Query query = new Query(criteria);

        List<Singer>  singers= mongoTemplate.find(query, Singer.class);

        return singers;

    }
}
