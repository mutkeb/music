package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import fm.douban.service.SongService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

@Service
public class SongServiceImpl implements SongService {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Song add(Song song) {
        return mongoTemplate.insert(song);
    }

    @Override
    public Song get(String songId) {
        return mongoTemplate.findById(songId,Song.class);
    }

    @Override
    public Page<Song> list(SongQueryParam songQueryParam) {
        Criteria criteria = new Criteria();
        List<Criteria> subCris = new ArrayList<>();

        if(StringUtils.hasText(songQueryParam.getName())){
            subCris.add(Criteria.where("name").is(songQueryParam.getName()));
        }

        if(StringUtils.hasText(songQueryParam.getLyrics())){
            subCris.add(Criteria.where("lyrics").is(songQueryParam.getLyrics()));
        }

        if(StringUtils.hasText(songQueryParam.getId())){
            subCris.add(Criteria.where("id").is(songQueryParam.getId()));
        }

        if(!subCris.isEmpty()){
            criteria.andOperator(subCris.toArray(new Criteria[]{}));
        }

        Query query = new Query(criteria);
        long count = mongoTemplate.count(query,Song.class);
        Pageable pageable = PageRequest.of(songQueryParam.getPageNum()-1,songQueryParam.getPageSize());
        query.with(pageable);
        List<Song> songs = mongoTemplate.find(query, Song.class);
        Page<Song> page = PageableExecutionUtils.getPage(songs, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });
        return page;
    }

    @Override
    public boolean modify(Song song) {
        Query query = new Query(Criteria.where("id").is(song.getId()));
        Update update = new Update();
        update.set("name",song.getName());
        update.set("gmtModified", LocalDateTime.now());
        UpdateResult result = mongoTemplate.updateFirst(query, update, Song.class);
        if(result.getModifiedCount() > 0){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean delete(String songId) {
        Song song = get(songId);
        DeleteResult remove = mongoTemplate.remove(song);
        if(remove.getDeletedCount() > 0){
            return true;
        }else{
            return false;
        }
    }

}
