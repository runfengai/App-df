package com.jarry.app.bean;

/**
 * Created by Jarry on 2018/5/7.
 */

public class AccountBean {
    public String name;
    public String password;
    public String description;
    public int id;

    public AccountBean(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public AccountBean(String name, String password, String description, int id) {
        this.name = name;
        this.password = password;
        this.description = description;
        this.id = id;
    }
}
