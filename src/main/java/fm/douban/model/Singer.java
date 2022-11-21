package fm.douban.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

public class Singer {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    //  主键id
    private String id;
    //  创建时间
    private LocalDateTime gmtCreated;
    //  修改时间
    private LocalDateTime gmtModified;
    //  名称
    private String name;
    //  头像
    private String avatar;
    //  主页
    private String homepage;
    //  相似的歌手id
    private List<String> similarSingerIds;

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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public List<String> getSimilarSingerIds() {
        return similarSingerIds;
    }

    public void setSimilarSingerIds(List<String> similarSingerIds) {
        this.similarSingerIds = similarSingerIds;
    }

}
