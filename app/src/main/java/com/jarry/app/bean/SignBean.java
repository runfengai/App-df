package com.jarry.app.bean;

import java.io.Serializable;

/**
 * Created by Jarry on 2018/5/1.
 */

public class SignBean implements Serializable, Cloneable {
    public String name;
    public String icon;
    public String orgnizeId;
    public String orgnizeName;
    public String description;
    public String createTime;
    public boolean hasParticipate;//参加
}
