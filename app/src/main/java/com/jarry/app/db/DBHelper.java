package com.jarry.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
*
*@Author jarry
*created at 2018/4/29 12:12
*/

public class DBHelper extends SQLiteOpenHelper {
    static final String DBNAME = "sqlite_db";
    static final int VERSION = 1;

    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建表语句
        db.execSQL(UserDao.getCreateSql());
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
