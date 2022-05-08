package fm.douban.service;

import fm.douban.model.Subject;

import java.util.List;

public interface SubjectService {
     //增加一个主题
     Subject addSubject(Subject subject);
     //查询单个主题
     Subject get(String subjectId);
     //查询一组主题
     List<Subject> getSubjects(String type,String subType);
     //查询一组主题
     List<Subject> getSubjects(String type);
     //删除一个主题
     boolean delete(String subjectId);
     //修改一个主题
     boolean modify(Subject subject);
     //多条件查询主题
     List<Subject> getSubjects(Subject subjectParam);
}
