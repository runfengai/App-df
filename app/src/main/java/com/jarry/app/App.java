package com.jarry.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.jarry.app.bean.User;
import com.jarry.app.db.DBHelper;
import com.jarry.app.util.PrefUtils;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jarry on 2018/2/25.
 */

public class App extends Application {
    private static App instance = null;
    private static List<Activity> list = new ArrayList<>();
    public static LiteOrm mDb;
    private static final String DB_NAME = "app.db";

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        new DBHelper(this);
        instance = this;
        mDb = LiteOrm.newCascadeInstance(this, DB_NAME);
        if (BuildConfig.DEBUG) {
            mDb.setDebugged(true);
        }
    }

    public static void addAct(Activity act) {
        list.add(act);
    }

    public static void removeAct(Activity act) {
        list.remove(act);
    }

    public static void exit() {
        for (Activity act : list) {
            act.finish();
        }
    }

    public static User getUser() {
        String userSp = PrefUtils.getString(App.instance, "userId", "");
        if (!TextUtils.isEmpty(userSp)) {
            QueryBuilder<User> qb = new QueryBuilder<>(User.class);
            qb.whereEquals("id", userSp);
            List<User> users = App.mDb.query(qb);
            users.get(0);
        }

        return null;
    }
}
