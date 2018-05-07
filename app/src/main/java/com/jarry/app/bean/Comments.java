package com.jarry.app.bean;

import java.io.Serializable;


public class Comments implements Serializable {

    private String created_at;
    private long id;
    private String idstr;
    private Status status;
    private User user;
    private String text;

    public String getCreated_at() {
        return created_at;
    }

    public long getId() {
        return id;
    }

    public String getIdstr() {
        return idstr;
    }

    public Status getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "Comments{" +
                "created_at='" + created_at + '\'' +
                ", id=" + id +
                ", idstr='" + idstr + '\'' +
                ", status=" + status +
                ", user=" + user +
                ", text='" + text + '\'' +
                '}';
    }
}
