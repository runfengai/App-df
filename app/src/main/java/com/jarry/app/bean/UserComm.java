package com.jarry.app.bean;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;

import java.io.Serializable;

/**
 * 用户
 */
@Table("usercomm")
public class UserComm implements Serializable {
    @PrimaryKey(AssignType.AUTO_INCREMENT)
    public long autoId; // id
    @Column("id")
    public long mId; // id
    @Column("name")
    public String mName; // 用户名


    public UserComm(long mId, String mName) {
        this.mId = mId;
        this.mName = mName;
    }
}
