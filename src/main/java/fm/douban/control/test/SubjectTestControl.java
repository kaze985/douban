package fm.douban.control.test;

import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/test/subject")
public class SubjectTestControl {
    @Autowired
    private SubjectService subjectService;

    @GetMapping(path = "/add")
    public Subject testAdd(){
        Subject subject = new Subject();
        subject.setId("0");
        subject.setName("李鹏鹏牛逼");
        subject.setSubjectType(SubjectUtil.TYPE_MHZ);
        subject.setSubjectSubType(SubjectUtil.TYPE_SUB_ARTIST);
        Subject addSubject = subjectService.addSubject(subject);
        return addSubject;
    }

    @GetMapping(path = "/get")
    public Subject testGet(){
        Subject subject = subjectService.get("0");
        return subject;
    }

    @GetMapping(path = "/del")
    public boolean testDelete(){
        boolean delete = subjectService.delete("0");
        return delete;
    }

    @GetMapping(path = "/getByType")
    public List<Subject> testGetByType(){
        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
        return subjects;
    }

    @GetMapping(path = "/getBySubType")
    public List<Subject> testGetBySubType(){
        List<Subject> subjects = subjectService.getSubjects(SubjectUtil.TYPE_MHZ,SubjectUtil.TYPE_SUB_ARTIST);
        return subjects;
    }
}
