package com.jarry.app.bean;

import java.io.Serializable;

/**
 * 用户
 */
public class UserComm implements Serializable {

    public long mId; // id
    public String mName; // 用户名


    public UserComm(long mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }
}
