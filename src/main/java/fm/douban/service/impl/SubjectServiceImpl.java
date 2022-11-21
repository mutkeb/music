package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
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
    private static final Logger LOG = LoggerFactory.getLogger(SubjectServiceImpl.class);

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
    public List<Subject> getSubjects(String type) {
        Subject subject = new Subject();
        subject.setSubjectType(type);
        return getSubjects(subject);
    }

    @Override
    public List<Subject> getSubjects(String type, String subType) {
        Subject subject = new Subject();
        subject.setSubjectType(type);
        subject.setSubjectSubType(subType);
        return getSubjects(subject);
    }

    @Override
    public boolean delete(String subjectId) {
        Subject subject = get(subjectId);
        DeleteResult remove = mongoTemplate.remove(subject);
        if(remove.getDeletedCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean modify(Subject subject) {
        Query query = new Query(Criteria.where("id").is(subject.getId()));
        Update update = new Update();
        update.set("songIds",subject.getSongIds());
        update.set("gmtModified", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Subject.class);
        if(result.getModifiedCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<Subject> getSubjects(Subject subjectParam) {
        Criteria criteria = new Criteria();
        List<Criteria> subCris = new ArrayList<>();
        if (subjectParam == null) {
            LOG.error("input subjectParam is not correct.");
            return null;
        }
        String type = subjectParam.getSubjectType();
        String subType = subjectParam.getSubjectSubType();
        if(StringUtils.hasText(subjectParam.getMaster())){
            subCris.add(Criteria.where("master").is(subjectParam.getMaster()));
        }

        if(StringUtils.hasText(type)){
            subCris.add(Criteria.where("subjectType").is(type));
        }

        if(StringUtils.hasText(subType)){
            subCris.add(Criteria.where("subjectSubType").is(subType));
        }

        if(!subCris.isEmpty()){
            criteria.andOperator(subCris.toArray(new Criteria[]{}));
        }
        Query query = new Query(criteria);
        return mongoTemplate.find(query,Subject.class);
    }
}
