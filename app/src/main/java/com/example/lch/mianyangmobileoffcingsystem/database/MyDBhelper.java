package com.example.lch.mianyangmobileoffcingsystem.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.lch.mianyangmobileoffcingsystem.bean.Sign;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by lch on 2017/3/2.
 */

public class MyDBhelper extends SQLiteOpenHelper {
    private static String dbName = "helper.db";
    private static int version = 1;
    private static String TABLE_NAME = "sign";
    private SQLiteDatabase db;

    public MyDBhelper(Context context) {
        super(context, dbName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.execSQL("create table sign(_id integer primary key autoincrement,score integer default '0',year integer default '1970',month integer default '1',day integer default '1',content text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db.execSQL("drop tale sign");
            onCreate(db);
        }
    }

    public void insert(String insertSql) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL(insertSql);
        db.close();
    }
    public void update(ContentValues values,String whereClause, String[] whereArgs) {
        getReadableDatabase().update(TABLE_NAME,values,whereClause,whereArgs);
    }

    public void delete(String name) {
        getReadableDatabase().delete(TABLE_NAME,"name=?",new String[]{name});
    }
    public List<Sign> query() {
        List<Sign> res = new ArrayList<Sign>();
        try {
            Cursor cursor = getReadableDatabase().rawQuery("select * from sign", null);
            while (cursor.moveToNext()) {
                Sign sign = new Sign();
                sign.setScore(cursor.getInt(cursor.getColumnIndex("score")));
                sign.setYear(cursor.getInt(cursor.getColumnIndex("year")));
                sign.setMonth(cursor.getInt(cursor.getColumnIndex("month")));
                sign.setDay(cursor.getInt(cursor.getColumnIndex("day")));
                sign.setWorkContent(cursor.getString(cursor.getColumnIndex("content")));
                res.add(sign);
            }
        }catch (SQLiteException e){
            e.printStackTrace();
            Log.e(TAG, "query failed");
        }
        return res;
    }
}
