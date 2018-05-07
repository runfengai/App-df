package com.jarry.app.db;

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

    public static List<Status> getAll() {
        List<Status> res = App.mDb.query(Status.class);
        return res;
    }

    public static List<Status> getMy() {
        List<Status> all = App.mDb.query(Status.class);
        List<Status> res = new ArrayList<>();
        for (Status status : all) {
            if (status.getUser().getScreen_name().equals(App.getUser().getScreen_name())) {
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
            for (Comment comment : comments) {
                if (comment.mCommentator.equals(App.getUser().getScreen_name())) {
                    res.add(status);
                    break;
                }
            }
        }

        return res;
    }

}
