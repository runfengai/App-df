package com.jarry.app.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Mapping;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.litesuits.orm.db.enums.Relation;

import java.io.Serializable;

/**
 * 评论对象
 */
@Table("comment")
public class Comment implements Serializable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public int id; // 评论内容
    @Column("content")
    public String mContent; // 评论内容
    @Mapping(Relation.OneToOne)
    public UserComm mCommentator; // 评论者
    @Mapping(Relation.OneToOne)
    public UserComm mReceiver; // 接收者（即回复谁）

    public Comment(UserComm mCommentator, String mContent, UserComm mReceiver) {
        this.mCommentator = mCommentator;
        this.mContent = mContent;
        this.mReceiver = mReceiver;
    }
}
