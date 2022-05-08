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
import org.thymeleaf.util.ListUtils;

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
    public Page<Song> list(SongQueryParam songParam) {
        Criteria criteria = new Criteria();
        List<Criteria> subCris=new ArrayList<>();
        if(StringUtils.hasText(songParam.getName())){
            subCris.add(Criteria.where("name").is(songParam.getName()));
        }
        if(StringUtils.hasText(songParam.getLyrics())){
            subCris.add(Criteria.where("lyrics").is(songParam.getLyrics()));
        }

        if(!subCris.isEmpty()){
            criteria.andOperator(subCris.toArray(new Criteria[]{}));
        }

        Query query = new Query(criteria);
        Pageable pageable=PageRequest.of(songParam.getPageNum()-1, songParam.getPageSize());
        query.with(pageable);
        long count = mongoTemplate.count(query, Song.class);
        List<Song> songs = mongoTemplate.find(query, Song.class);
        Page<Song>  pageResult= PageableExecutionUtils.getPage(songs, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });
        return pageResult;
    }

    @Override
    public boolean modify(Song song) {
        if(song==null){
            return false;
        }
        Query query = new Query(Criteria.where("id").is(song.getId()));
        Update update = new Update();
        if (StringUtils.hasText(song.getCover())){
            update.set("cover",song.getCover());
        }
        if (StringUtils.hasText(song.getLyrics())){
            update.set("lyrics",song.getLyrics());
        }
        if (StringUtils.hasText(song.getName())){
            update.set("name",song.getName());
        }
        if (!ListUtils.isEmpty(song.getSingerIds())){
            update.set("singerIds",song.getSingerIds());
        }
        if (StringUtils.hasText(song.getUrl())){
            update.set("url",song.getUrl());
        }
        UpdateResult result = mongoTemplate.updateFirst(query, update, Song.class);
        return result!=null&&result.getModifiedCount()>0;

    }

    @Override
    public boolean delete(String songId) {
        Song song = new Song();
        song.setId(songId);
        DeleteResult result = mongoTemplate.remove(song);
        return result!=null&&result.getDeletedCount()>0;
    }
}
