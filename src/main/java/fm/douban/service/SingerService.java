package fm.douban.service;

import fm.douban.model.Singer;

import java.util.List;

public interface SingerService {
    //  增加一个歌手
    public Singer addSinger(Singer singer);
    //  根据歌手id查询歌手
    public Singer get(String singerId);
    //  查询全部歌手
    public List<Singer> getAll();
    //  修改歌手，只能修改、名称、头像、主页、相似的歌手id
    public boolean modify(Singer singer);
    //  根据id主键删除歌手
    public boolean delete(String singerId);
}
