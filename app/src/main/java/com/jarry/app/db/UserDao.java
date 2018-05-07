package com.jarry.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.jarry.app.bean.AccountBean;

import java.util.ArrayList;
import java.util.List;


/**
 * @Author jarry
 * created at 2018/4/29 12:24
 */

public class UserDao {

    private SQLiteDatabase database;

    public static class Table {
        //表
        public static final String T_USAGE = "t_usage";
        //字段
        public static final String I_ID = "i_id";//主键
        public static final String NAME = "name";//名字
        public static final String PASSWORD = "password";//密码
        public static final String DESCRIPTION = "description";//
        public static final String IMG_URL = "img_url";//
    }

    //建表语句
    private static StringBuilder createSqlSb = new StringBuilder();

    static {
        createSqlSb.append("CREATE TABLE IF NOT EXISTS ")
                .append(Table.T_USAGE)
                .append(" ( ")
                .append(Table.I_ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(Table.NAME).append(" VARCHAR NOT NULL,")
                .append(Table.PASSWORD).append(" VARCHAR NOT NULL,")
                .append(Table.DESCRIPTION).append(" VARCHAR,")
                .append(Table.IMG_URL).append(" VARCHAR")
                .append(" ) ");
    }

    /**
     * 建表语句
     *
     * @return
     */
    public static String getCreateSql() {
        return createSqlSb.toString();
    }


    public UserDao(Context context) {
        DBHelper DBHelper = new DBHelper(context);
        database = DBHelper.getWritableDatabase();
    }

    /**
     * @param
     * @return
     */
    public AccountBean checkAccount(String name, String password) {
        Cursor cursor = database.query(Table.T_USAGE, null, Table.NAME + "= ? and " + Table.PASSWORD + " =? ", new String[]{name, password}, null, null, null);
        if (cursor.getColumnCount() < 1) return null;//查不到
        AccountBean eleUsage = null;
        List<AccountBean> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            eleUsage = new AccountBean(
                    cursor.getString(cursor.getColumnIndex(Table.NAME)),
                    cursor.getString(cursor.getColumnIndex(Table.PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(Table.DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(Table.I_ID)));
        }
        cursor.close();
        return eleUsage;
    }

    /**
     * @param
     * @return
     */
    public AccountBean getAccount(String name) {
        Cursor cursor = database.query(Table.T_USAGE, null, Table.NAME + "= ? ", new String[]{name}, null, null, null);
        if (cursor.getColumnCount() < 1) return null;//查不到
        AccountBean eleUsage = null;
        List<AccountBean> list = new ArrayList<>();
        if (cursor.moveToNext()) {
            eleUsage = new AccountBean(
                    cursor.getString(cursor.getColumnIndex(Table.NAME)),
                    cursor.getString(cursor.getColumnIndex(Table.PASSWORD)),
                    cursor.getString(cursor.getColumnIndex(Table.DESCRIPTION)),
                    cursor.getInt(cursor.getColumnIndex(Table.I_ID)));
        }
        cursor.close();
        return eleUsage;
    }

    /**
     * 插入数据
     *
     * @param eleUsage
     * @return
     */
    public Long insert(AccountBean eleUsage) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Table.NAME, eleUsage.name);
        contentValues.put(Table.PASSWORD, eleUsage.password);
        contentValues.put(Table.DESCRIPTION, eleUsage.description);

        long res = database.replace(Table.T_USAGE, null, contentValues);
        return res;
    }
}

