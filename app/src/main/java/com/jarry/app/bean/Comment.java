package com.jarry.app.bean;

import java.io.Serializable;

/**
 * 评论对象
 */
public class Comment  implements Serializable {
    public String mContent; // 评论内容
    public UserComm mCommentator; // 评论者
    public UserComm mReceiver; // 接收者（即回复谁）

    public Comment(UserComm mCommentator, String mContent, UserComm mReceiver) {
        this.mCommentator = mCommentator;
        this.mContent = mContent;
        this.mReceiver = mReceiver;
    }
}
