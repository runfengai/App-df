package com.jarry.app.bean;

import com.google.gson.Gson;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;
import java.util.ArrayList;

@Table("status")
public class Status implements Serializable, Cloneable {

    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public Long autoId;
    @Column("created_at")
    private String created_at;
    @PrimaryKey(AssignType.BY_MYSELF)
    @Column("id")
    private String id;
    @Column("idstr")
    private String idstr;
    @Column("text")
    private String text;
    @Column("source")
    private String source;
    @Column("thumbnail_pic")
    private String thumbnail_pic;
    @Column("bmiddle_pic")
    private String bmiddle_pic;
    @Column("original_pic")
    private String original_pic;
    @Column("reposts_count")
    private String reposts_count;
    @Column("comments_count")
    private String comments_count;
    @Column("attitudes_count")
    private String attitudes_count;
    @Mapping(Relation.OneToOne)
    private Status retweeted_status;
    @Column("retweeted_statusStr")
    public String retweeted_statusStr;
    @Mapping(Relation.OneToMany)
    private ArrayList<ThumbnailPic> pic_urls;

    @Mapping(Relation.ManyToOne)
    private User user;
    @Column("userstr")
    public String userStr;
    @Mapping(Relation.OneToMany)
    public ArrayList<Comment> mComment; // 评论列表
    @Column("mcommentstr")
    public String mCommentStr; // 评论列表
    @Mapping(Relation.OneToMany)
    public ArrayList<User> likeUsers; // 点赞列表
    @Column("likeUsersStr")
    public String likeUsersStr;

    public void setRetweeted_statusStr() {
        this.retweeted_statusStr =new Gson().toJson(retweeted_status);
    }

    public void setLikeUsersStr() {
        this.likeUsersStr = new Gson().toJson(likeUsers);
    }

    public void setmCommentStr() {
        this.mCommentStr = new Gson().toJson(mComment);
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getId() {
        return id;
    }

    public String getIdstr() {
        return idstr;
    }

    public Status getRetweeted_status() {
        return retweeted_status;
    }

    public String getAttitudes_count() {
        return attitudes_count;
    }

    public String getComments_count() {
        return comments_count;
    }

    public String getReposts_count() {
        return reposts_count;
    }

    public String getOriginal_pic() {
        return original_pic;
    }

    public String getBmiddle_pic() {
        return bmiddle_pic;
    }

    public String getThumbnail_pic() {
        return thumbnail_pic;
    }

    public String getSource() {
        return source;
    }

    public String getText() {
        return text;
    }

    public ArrayList<ThumbnailPic> getPic_urls() {
        return pic_urls;
    }

    public User getUser() {
        return user;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setThumbnail_pic(String thumbnail_pic) {
        this.thumbnail_pic = thumbnail_pic;
    }

    public void setBmiddle_pic(String bmiddle_pic) {
        this.bmiddle_pic = bmiddle_pic;
    }

    public void setOriginal_pic(String original_pic) {
        this.original_pic = original_pic;
    }

    public void setReposts_count(String reposts_count) {
        this.reposts_count = reposts_count;
    }

    public void setComments_count(String comments_count) {
        this.comments_count = comments_count;
    }

    public void setAttitudes_count(String attitudes_count) {
        this.attitudes_count = attitudes_count;
    }

    public void setRetweeted_status(Status retweeted_status) {
        this.retweeted_status = retweeted_status;
    }

    public void setPic_urls(ArrayList<ThumbnailPic> pic_urls) {
        this.pic_urls = pic_urls;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Comment> getmComment() {
        return mComment;
    }

    public void setmComment(ArrayList<Comment> mComment) {
        this.mComment = mComment;
    }

    public ArrayList<User> getLikeUsers() {
        return likeUsers;
    }

    public void setLikeUsers(ArrayList<User> likeUsers) {
        this.likeUsers = likeUsers;
    }

    @Table("thumbnailpic")
    public static class ThumbnailPic implements Serializable {
        @PrimaryKey(AssignType.AUTO_INCREMENT)
        private int autoId;
        @Column("thumbnail_pic")
        private String thumbnail_pic;
        @Column("localpic")
        public String localPic = "";

        public void setThumbnail_pic(String thumbnail_pic) {
            this.thumbnail_pic = thumbnail_pic;
        }

        public String getLargeImg() {
            return thumbnail_pic.replace("thumbnail", "large");
        }

        public String getSmallImg() {
            return thumbnail_pic.replace("thumbnail", "small");
        }

        public String getThumbnail_pic() {
            return thumbnail_pic;
        }

        public String getImage() {
            if (thumbnail_pic == null) return "";
            if (thumbnail_pic.contains("thumbnail")) {
                return getThumbnail_pic().replace("thumbnail", "large");
            } else {
                return getThumbnail_pic().replace("small", "large");
            }
        }
    }


    @Override
    public String toString() {
        return "Status{" +
                "created_at='" + created_at + '\'' +
                ", id=" + id +
                ", idstr='" + idstr + '\'' +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                ", thumbnail_pic='" + thumbnail_pic + '\'' +
                ", bmiddle_pic='" + bmiddle_pic + '\'' +
                ", original_pic='" + original_pic + '\'' +
                ", reposts_count='" + reposts_count + '\'' +
                ", comments_count='" + comments_count + '\'' +
                ", attitudes_count='" + attitudes_count + '\'' +
                ", retweeted_status=" + retweeted_status +
                ", pic_urls=" + pic_urls +
                ", user=" + user +
                '}';
    }

}
