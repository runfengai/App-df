package com.jarry.app.db;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jarry.app.App;
import com.jarry.app.bean.Comment;
import com.jarry.app.bean.Status;
import com.jarry.app.bean.User;
import com.litesuits.orm.db.assit.QueryBuilder;

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
        List<Status> res = App.mDb.query(new QueryBuilder<>(Status.class).appendOrderDescBy("created_at"));
        for (Status status : res) {
            try {
                status.setRetweeted_status(gson.fromJson(status.retweeted_statusStr, Status.class));
            } catch (Exception e) {
            }

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
        List<Status> all = App.mDb.query(new QueryBuilder<>(Status.class).appendOrderDescBy("created_at"));
        List<Status> res = new ArrayList<>();
        for (Status status : all) {
            try {
                status.setRetweeted_status(gson.fromJson(status.retweeted_statusStr, Status.class));
            } catch (Exception e) {
            }

            status.setUser(gson.fromJson(status.userStr, User.class));
            if (status.getUser().getScreen_name().equals(App.getUser().getScreen_name())) {
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
        List<Status> all = App.mDb.query(new QueryBuilder<>(Status.class).appendOrderDescBy("created_at"));
        List<Status> res = new ArrayList<>();
        for (Status status : all) {
            try {
                status.setRetweeted_status(gson.fromJson(status.retweeted_statusStr, Status.class));
            } catch (Exception e) {
            }

            status.setLikeUsers(
                    gson.fromJson(status.likeUsersStr, new TypeToken<ArrayList<User>>() {
                    }.getType()));
            List<User> users = status.getLikeUsers();
            if (users != null && users.size() > 0) {
                for (User user : users) {
                    if (user == null) continue;
                    if (user.getName().equals(App.getUser().getScreen_name())) {
                        status.setUser(gson.fromJson(status.userStr, User.class));
                        status.setmComment(
                                gson.fromJson(status.mCommentStr, new TypeToken<ArrayList<Comment>>() {
                                }.getType()));
                        res.add(status);
                        break;
                    }
                }
            }
        }

        return res;
    }

    public static List<Status> getComments() {
        List<Status> all = App.mDb.query(new QueryBuilder<>(Status.class).appendOrderDescBy("created_at"));
        List<Status> res = new ArrayList<>();
        for (Status status : all) {
            try {
                status.setRetweeted_status(gson.fromJson(status.retweeted_statusStr, Status.class));
            } catch (Exception e) {
            }

            status.setmComment(
                    gson.fromJson(status.mCommentStr, new TypeToken<ArrayList<Comment>>() {
                    }.getType()));
            List<Comment> comments = status.getmComment();
            if (comments == null) continue;
            for (Comment comment : comments) {
                if (comment.mCommentator == null) {
                    continue;
                }
                if (comment.mCommentator.mName != null && comment.mCommentator.mName.equals(App.getUser().getScreen_name())) {
                    status.setUser(gson.fromJson(status.userStr, User.class));

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
