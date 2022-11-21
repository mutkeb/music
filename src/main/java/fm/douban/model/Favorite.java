package fm.douban.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class Favorite {
    //  主键id
    private String id;
    //  创建时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtCreated;
    //  修改时间
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime gmtModified;
    //  用户id
    private String userId;
    //  喜欢的类型
    private String type;
    //  喜欢的目标：歌曲/歌手/mhz等
    private String itemType;
    //  喜欢的目标的id
    private String itemId;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
