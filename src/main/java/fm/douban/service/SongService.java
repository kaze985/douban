package fm.douban.service;

import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import org.springframework.data.domain.Page;

public interface SongService {
    //增加一个歌曲
    Song add(Song song);
    //根据id查询
    Song get(String songId);
    //查询全部歌曲
    Page<Song> list(SongQueryParam songParam);
    //修改歌曲信息
    boolean modify(Song song);
    //删除歌曲
    boolean delete(String songId);

}
