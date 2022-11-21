package fm.douban.model;

import java.time.LocalDateTime;
import java.util.List;

public class Song {
    //  主键id
    private String id;
    //  创建时间
    private LocalDateTime gmtCreated;
    //  修改时间
    private LocalDateTime gmtModified;
    //  名称
    private String name;
    //  歌词
    private String lyrics;
    //  封面图
    private String cover;
    //  播放地址
    private String url;
    //  所属歌手id，可能一首歌由多个歌手合唱
    private List<String> singerIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getGmtCreated() {
        return gmtCreated;
    }

    public void setGmtCreated(LocalDateTime gmtCreated) {
        this.gmtCreated = gmtCreated;
    }

    public LocalDateTime getGmtModified() {
        return gmtModified;
    }

    public void setGmtModified(LocalDateTime gmtModified) {
        this.gmtModified = gmtModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getSingerIds() {
        return singerIds;
    }

    public void setSingerIds(List<String> singerIds) {
        this.singerIds = singerIds;
    }
}
