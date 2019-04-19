package com.example.kunrui.apcheck.MethodsClass;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlOpr extends SQLiteOpenHelper {
    private static String wifiMsg = "wifiMsg";

    public SqlOpr(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static final String CREATE_WIFIMSG = "create table if not exists "+
            wifiMsg+
            "(id integer primary key autoincrement, "+
            "SSID text," +
            "PASSWORD text," +
            "isFirst boolen," +
            "Address text," +
            "Encrypt text" +
            ");";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WIFIMSG);
        db.execSQL("insert into wifiMsg(SSID, PASSWORD, isFirst, Address, Encrypt) values('OpenWrt', '65HP7FW4', '1', 'http://192.168.1.1', 'WPA');");
        db.execSQL("insert into wifiMsg(SSID, PASSWORD, isFirst, Address, Encrypt) values('KunRui_2.4G', '12345678', '0', 'https://www.baidu.com/', 'ESS');");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+ wifiMsg);
        onCreate(db);
    }

    public void resetSQL(SQLiteDatabase db) {
        db.execSQL("drop table if exists "+ wifiMsg);
        onCreate(db);
    }
}