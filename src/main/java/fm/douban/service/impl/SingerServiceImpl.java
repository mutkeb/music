package fm.douban.service.impl;


import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SingerServiceImpl implements SingerService {

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
        Query query = new Query(Criteria.where("id").is(singer.getId()));
        Update update = new Update();
        update.set("name", singer.getName());
        update.set("gmtModified", LocalDateTime.now());
        update.set("avater",singer.getAvatar());
        update.set("homepage",singer.getHomepage());
        update.set("similarSingerIds",singer.getSimilarSingerIds());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Singer.class);
        if(result.getModifiedCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean delete(String singerId) {
        Singer singer = get(singerId);
        DeleteResult remove = mongoTemplate.remove(singer);
        if(remove.getDeletedCount() > 0){
            return true;
        }else{
            return false;
        }
    }
}
