package fm.douban.service;

import fm.douban.model.Favorite;

import java.util.List;

public interface FavoriteService {
    //新增一个喜欢
    Favorite add(Favorite fav);
    //计算喜欢数，>0表示已经喜欢
    List<Favorite> list(Favorite favParam);
    //删除一个喜欢
    boolean delete(Favorite favParam);
}
