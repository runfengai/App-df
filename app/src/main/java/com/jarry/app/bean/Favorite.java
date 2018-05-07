package com.jarry.app.bean;

import java.io.Serializable;


public class Favorite implements Serializable {

    private String favorited_time;
    private Status status;

    public String getFavorited_time() {
        return favorited_time;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "favorited_time='" + favorited_time + '\'' +
                ", status=" + status +
                '}';
    }
}
