package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Song;
import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
public class SubjectServiceImpl implements SubjectService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Subject addSubject(Subject subject) {
        return mongoTemplate.insert(subject);
    }

    @Override
    public Subject get(String subjectId) {
        return mongoTemplate.findById(subjectId,Subject.class);
    }

    @Override
    public List<Subject> getSubjects(String type, String subType) {
        Subject subjectParam = new Subject();
        subjectParam.setSubjectType(type);
        subjectParam.setSubjectSubType(subType);

        return getSubjects(subjectParam);

    }

    @Override
    public List<Subject> getSubjects(Subject subjectParam) {
        if (subjectParam == null) {
            LOG.error("input subjectParam is not correct.");
            return null;
        }

        Criteria criteria = new Criteria();
        List<Criteria> subCris=new ArrayList<>();

        if(StringUtils.hasText(subjectParam.getName())){
            subCris.add(Criteria.where("name").is(subjectParam.getName()));
        }
        if(StringUtils.hasText(subjectParam.getId())){
            subCris.add(Criteria.where("id").is(subjectParam.getId()));
        }
        if(StringUtils.hasText(subjectParam.getMaster())){
            subCris.add(Criteria.where("master").is(subjectParam.getMaster()));
        }
        if(StringUtils.hasText(subjectParam.getSubjectType())){
            subCris.add(Criteria.where("subjectType").is(subjectParam.getSubjectType()));
        }
        if(StringUtils.hasText(subjectParam.getSubjectSubType())){
            subCris.add(Criteria.where("subjectSubType").is(subjectParam.getSubjectSubType()));
        }

        if(!subCris.isEmpty()){
            criteria.andOperator(subCris.toArray(new Criteria[]{}));
        }
        Query query = new Query(criteria);
        List<Subject> subjects = mongoTemplate.find(query, Subject.class);

        return subjects;
    }

    @Override
    public List<Subject> getSubjects(String type) {
        Criteria criteria = Criteria.where("subjectType").is(type);
        Query query = new Query(criteria);
        List<Subject> subjects = mongoTemplate.find(query, Subject.class);
        return subjects;
    }

    @Override
    public boolean delete(String subjectId) {
        Subject subject = new Subject();
        subject.setId(subjectId);
        DeleteResult result = mongoTemplate.remove(subject);
        return result!=null&&result.getDeletedCount()>0;

    }

    @Override
    public boolean modify(Subject subject) {
        Query query = new Query(Criteria.where("id").is(subject.getId()));
        Update update= new Update();
        update.set("songIds",subject.getSongIds());
        update.set("gmtModified", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Subject.class);
        return result!=null&&result.getModifiedCount()>0;
    }


}

