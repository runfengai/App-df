package com.jarry.app.db;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jarry.app.App;
import com.jarry.app.bean.Comment;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jarry on 2018/5/8.
 */

public class StatusDao {
    public static void save(Status status) {
        App.mDb.insert(status);
    }

    static Gson gson = new Gson();

    public static List<Status> getAll() {
        List<Status> res = App.mDb.query(Status.class);
        for (Status status : res) {
            status.setUser(gson.fromJson(status.userStr, User.class));
            status.setmComment(
                    gson.fromJson(status.mCommentStr, new TypeToken<ArrayList<Comment>>() {
                    }.getType()));
            status.setLikeUsers(
                    gson.fromJson(status.likeUsersStr, new TypeToken<ArrayList<User>>() {
                    }.getType()));
        }
        return res;
    }

    public static List<Status> getMy() {
        List<Status> all = App.mDb.query(Status.class);
        List<Status> res = new ArrayList<>();
        for (Status status : all) {
            if (status.getUser().getScreen_name().equals(App.getUser().getScreen_name())) {
                status.setUser(gson.fromJson(status.userStr, User.class));
                status.setmComment(
                        gson.fromJson(status.mCommentStr, new TypeToken<ArrayList<Comment>>() {
                        }.getType()));
                status.setLikeUsers(
                        gson.fromJson(status.likeUsersStr, new TypeToken<ArrayList<User>>() {
                        }.getType()));

                res.add(status);
            }
        }

        return res;
    }

    public static List<Status> getLike() {
        List<Status> all = App.mDb.query(Status.class);
        List<Status> res = new ArrayList<>();
        for (Status status : all) {
            List<User> users = status.getLikeUsers();
            for (User user : users) {
                if (user.getScreen_name().equals(App.getUser().getScreen_name())) {
                    status.setUser(gson.fromJson(status.userStr, User.class));
                    status.setmComment(
                            gson.fromJson(status.mCommentStr, new TypeToken<ArrayList<Comment>>() {
                            }.getType()));
                    status.setLikeUsers(
                            gson.fromJson(status.likeUsersStr, new TypeToken<ArrayList<User>>() {
                            }.getType()));
                    res.add(status);
                    break;
                }
            }
        }

        return res;
    }

    public static List<Status> getComments() {
        List<Status> all = App.mDb.query(Status.class);
        List<Status> res = new ArrayList<>();
        for (Status status : all) {
            List<Comment> comments = status.getmComment();
            if (comments == null) continue;
            for (Comment comment : comments) {
                if (comment.mCommentator == null) {
                    continue;
                }
                if (comment.mCommentator.mName != null && comment.mCommentator.mName.equals(App.getUser().getScreen_name())) {
                    status.setUser(gson.fromJson(status.userStr, User.class));
                    status.setmComment(
                            gson.fromJson(status.mCommentStr, new TypeToken<ArrayList<Comment>>() {
                            }.getType()));
                    status.setLikeUsers(
                            gson.fromJson(status.likeUsersStr, new TypeToken<ArrayList<User>>() {
                            }.getType()));
                    res.add(status);
                    break;
                }
            }
        }

        return res;
    }

}
